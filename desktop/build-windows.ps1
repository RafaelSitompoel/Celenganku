$ErrorActionPreference = 'Stop'

$projectDirectory = $PSScriptRoot
$outputDirectory = Join-Path $projectDirectory 'release\no-maintenance-popup'
$buildDirectory = Join-Path $projectDirectory 'build\native'
$iconPath = Join-Path $projectDirectory 'app.ico'
New-Item -ItemType Directory -Path $outputDirectory -Force | Out-Null
New-Item -ItemType Directory -Path $buildDirectory -Force | Out-Null

function Build-GuiExecutable {
    param(
        [string[]]$SourcePaths,
        [string]$OutputPath,
        [string]$Icon,
        [string[]]$Resources = @(),
        [string[]]$References = @()
    )

    $provider = New-Object Microsoft.CSharp.CSharpCodeProvider
    $parameters = New-Object System.CodeDom.Compiler.CompilerParameters
    $parameters.GenerateExecutable = $true
    $parameters.OutputAssembly = $OutputPath
    $parameters.CompilerOptions = '/target:winexe /platform:anycpu /optimize+ /win32icon:"' + $Icon + '"'
    foreach ($reference in @('System.dll', 'System.Core.dll', 'System.Drawing.dll', 'System.Windows.Forms.dll', 'Microsoft.CSharp.dll', 'Microsoft.VisualBasic.dll') + $References) {
        [void]$parameters.ReferencedAssemblies.Add($reference)
    }
    foreach ($resource in $Resources) {
        [void]$parameters.EmbeddedResources.Add($resource)
    }

    $result = $provider.CompileAssemblyFromFile($parameters, $SourcePaths)
    if ($result.Errors.HasErrors) {
        $messages = $result.Errors | ForEach-Object { $_.ToString() }
        throw ($messages -join [Environment]::NewLine)
    }
    $provider.Dispose()
}

$appExe = Join-Path $buildDirectory 'Celenganku.exe'
$standaloneAppExe = Join-Path $outputDirectory 'Celenganku.exe'
if (Test-Path $standaloneAppExe) {
    Remove-Item -LiteralPath $standaloneAppExe -Force
}
$uninstallerExe = Join-Path $buildDirectory 'Uninstall-Celenganku.exe'
$setupExe = Join-Path $outputDirectory 'Celenganku-Windows11.exe'

Build-GuiExecutable `
    -SourcePaths @((Join-Path $projectDirectory 'NativeSavingsApp.cs'), (Join-Path $projectDirectory 'AssemblyInfo.cs')) `
    -OutputPath $appExe `
    -Icon $iconPath `
    -References @('System.Web.Extensions.dll', 'System.IO.Compression.dll', 'System.IO.Compression.FileSystem.dll')

Build-GuiExecutable `
    -SourcePaths @((Join-Path $projectDirectory 'NativeUninstaller.cs'), (Join-Path $projectDirectory 'AssemblyInfo.cs')) `
    -OutputPath $uninstallerExe `
    -Icon $iconPath

Build-GuiExecutable `
    -SourcePaths @((Join-Path $projectDirectory 'NativeInstaller.cs'), (Join-Path $projectDirectory 'AssemblyInfo.cs')) `
    -OutputPath $setupExe `
    -Icon $iconPath `
    -Resources @($appExe, $uninstallerExe, $iconPath)

Write-Host "Installer Windows 11 siap: $setupExe"