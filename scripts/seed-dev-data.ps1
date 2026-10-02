<#
.SYNOPSIS
    Popula o ambiente local via chamadas HTTP reais à API (sem acesso direto ao banco),
    criando usuários, grupos e vínculos de membros (group_members) com estados variados
    para testar as funcionalidades da Release 2 (Grupos e Membros).

.DESCRIPTION
    1. Cria 10 usuários (POST /api/v1/user/create).
    2. Autentica cada usuário (POST /auth/login) para obter o access token.
    3. Cria 20 grupos distribuídos entre os 10 usuários (2 grupos por usuário),
       o criador de cada grupo vira automaticamente OWNER (POST /api/v1/groups/create).
    4. Para cada usuário, solicita entrada (PENDING) em alguns grupos dos quais não é dono
       (POST /api/v1/group/{groupId}/members/associate).
    5. O OWNER de cada grupo aprova parte das solicitações, rejeita outra parte e deixa
       o restante em PENDING, gerando uma massa com os status APPROVED, REJECTED e PENDING
       (PATCH /api/v1/group/{groupId}/members/{memberId}/status).

.PARAMETER BaseUrl
    URL base da API. Padrão: http://localhost:8080

.EXAMPLE
    .\scripts\seed-dev-data.ps1
    .\scripts\seed-dev-data.ps1 -BaseUrl "http://localhost:8080"
#>

param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Path,
        [hashtable]$Body = $null,
        [string]$Token = $null
    )

    $headers = @{ "Content-Type" = "application/json" }
    if ($Token) { $headers["Authorization"] = "Bearer $Token" }

    $uri = "$BaseUrl$Path"
    $jsonBody = $null
    if ($Body) { $jsonBody = $Body | ConvertTo-Json -Depth 6 }

    try {
        if ($jsonBody) {
            return Invoke-RestMethod -Method $Method -Uri $uri -Headers $headers -Body $jsonBody
        }
        return Invoke-RestMethod -Method $Method -Uri $uri -Headers $headers
    }
    catch {
        $resp = $_.Exception.Response
        if ($resp) {
            $reader = New-Object System.IO.StreamReader($resp.GetResponseStream())
            $errorBody = $reader.ReadToEnd()
            Write-Warning "Falha em $Method $Path -> $errorBody"
        }
        else {
            Write-Warning "Falha em $Method $Path -> $($_.Exception.Message)"
        }
        return $null
    }
}

Write-Host "==> Seed de dados de desenvolvimento em $BaseUrl" -ForegroundColor Cyan

# ----------------------------------------------------------------------------
# 1. Criação de 10 usuários
# ----------------------------------------------------------------------------
$userCount = 10
$users = @()

Write-Host "`n==> Criando $userCount usuarios..." -ForegroundColor Cyan
for ($i = 1; $i -le $userCount; $i++) {
    $idx = "{0:D2}" -f $i
    $username = "seeduser$idx"            # <= 10 caracteres
    $phone = "1190000$idx" + "0"           # 11 digitos, unico

    $body = @{
        name         = "Usuario Seed $idx"
        birthDate    = "01/01/1995"
        phone        = $phone
        profilePhoto = $null
        username     = $username
        password     = "Senha123!"
    }

    $created = Invoke-Api -Method POST -Path "/api/v1/user/create" -Body $body
    if ($created) {
        Write-Host "  [OK] usuario criado: $username"
    }

    $users += [pscustomobject]@{
        Username = $username
        Password = "Senha123!"
        Token    = $null
    }
}

# ----------------------------------------------------------------------------
# 2. Login de cada usuário
# ----------------------------------------------------------------------------
Write-Host "`n==> Autenticando usuarios..." -ForegroundColor Cyan
foreach ($user in $users) {
    $loginBody = @{ username = $user.Username; password = $user.Password }
    $auth = Invoke-Api -Method POST -Path "/auth/login" -Body $loginBody

    if ($auth -and $auth.accessToken) {
        $user.Token = $auth.accessToken
    }

    if ($user.Token) {
        Write-Host "  [OK] login: $($user.Username)"
    }
    else {
        Write-Warning "  [FALHA] login: $($user.Username) -- resposta: $($auth | ConvertTo-Json -Depth 5)"
    }
}

$authenticatedUsers = $users | Where-Object { $_.Token }
if ($authenticatedUsers.Count -eq 0) {
    throw "Nenhum usuario autenticado. Abortando seed."
}

# ----------------------------------------------------------------------------
# 3. Criação de 20 grupos (2 por usuário autenticado)
# ----------------------------------------------------------------------------
$cities = @(
    @{ City = "Salvador"; State = "Bahia" },
    @{ City = "Feira de Santana"; State = "Bahia" },
    @{ City = "Cruz das Almas"; State = "Bahia" },
    @{ City = "Sao Paulo"; State = "Sao Paulo" },
    @{ City = "Rio de Janeiro"; State = "Rio de Janeiro" }
)

$groups = @()
$groupsPerUser = 2
$groupSeq = 1

Write-Host "`n==> Criando grupos (2 por usuario)..." -ForegroundColor Cyan
foreach ($owner in $authenticatedUsers) {
    for ($g = 1; $g -le $groupsPerUser; $g++) {
        $location = $cities[($groupSeq - 1) % $cities.Count]
        $groupName = "Grupo Seed $("{0:D2}" -f $groupSeq)"

        $body = @{
            name        = $groupName
            description = "Grupo de teste gerado automaticamente ($groupName)"
            city        = $location.City
            state       = $location.State
        }

        $created = Invoke-Api -Method POST -Path "/api/v1/groups/create" -Body $body -Token $owner.Token

        if ($created -and $created.group) {
            $groups += [pscustomobject]@{
                Id    = $created.group.id
                Name  = $groupName
                Owner = $owner
            }
            Write-Host "  [OK] grupo criado: $groupName (owner: $($owner.Username))"
        }
        else {
            Write-Warning "  [FALHA] grupo nao criado: $groupName (owner: $($owner.Username))"
        }

        $groupSeq++
    }
}

if ($groups.Count -eq 0) {
    throw "Nenhum grupo criado. Abortando seed."
}

# ----------------------------------------------------------------------------
# 4. Solicitações de entrada (membership) em grupos de outros usuarios
# ----------------------------------------------------------------------------
Write-Host "`n==> Solicitando entrada de usuarios em grupos..." -ForegroundColor Cyan

# cada usuario solicita entrada em ate 3 grupos dos quais nao e dono
$maxRequestsPerUser = 3

foreach ($requester in $authenticatedUsers) {
    $candidateGroups = $groups | Where-Object { $_.Owner.Username -ne $requester.Username }
    $selected = $candidateGroups | Get-Random -Count ([Math]::Min($maxRequestsPerUser, $candidateGroups.Count))

    foreach ($group in $selected) {
        $result = Invoke-Api -Method POST -Path "/api/v1/group/$($group.Id)/members/associate" -Token $requester.Token

        if ($result) {
            Write-Host "  [OK] $($requester.Username) solicitou entrada em '$($group.Name)'"
        }
    }
}

# ----------------------------------------------------------------------------
# 5. Donos decidem sobre as solicitações: aprovam, rejeitam ou deixam pendente
# ----------------------------------------------------------------------------
Write-Host "`n==> Owners decidindo sobre solicitacoes pendentes..." -ForegroundColor Cyan

foreach ($group in $groups) {
    $pending = Invoke-Api -Method GET `
        -Path "/api/v1/group/$($group.Id)/members/search?status=PENDING&size=100" `
        -Token $group.Owner.Token

    if (-not $pending -or -not $pending.members) { continue }

    $members = $pending.members.content
    if (-not $members -or $members.Count -eq 0) { continue }

    for ($i = 0; $i -lt $members.Count; $i++) {
        $member = $members[$i]

        # distribui: 1/3 aprovado, 1/3 rejeitado, 1/3 permanece pendente
        $decision = $i % 3
        if ($decision -eq 2) {
            continue # mantem PENDING
        }

        $status = if ($decision -eq 0) { "APPROVED" } else { "REJECTED" }

        $body = @{ status = $status }
        $updated = Invoke-Api -Method PATCH `
            -Path "/api/v1/group/$($group.Id)/members/$($member.id)/status" `
            -Body $body -Token $group.Owner.Token

        if ($updated) {
            Write-Host "  [OK] grupo '$($group.Name)': membro $($member.name) -> $status"
        }
    }
}

Write-Host "`n==> Seed finalizado." -ForegroundColor Green
Write-Host "Usuarios: $($authenticatedUsers.Count) | Grupos: $($groups.Count)"
