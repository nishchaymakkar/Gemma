$files = Get-ChildItem -Path "d:\downloads\Gemma\app\src\main\java\com\gemma\app\presentation" -Recurse -Filter *.kt

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    $originalContent = $content
    
    $content = $content -replace 'Color\(0xFFFAFAFA\)', 'MaterialTheme.colorScheme.background'
    $content = $content -replace 'Color\(0xFFF0F0F0\)', 'MaterialTheme.colorScheme.surfaceVariant'
    $content = $content -replace 'Color\(0xFF141414\)', 'MaterialTheme.colorScheme.onSurface'
    $content = $content -replace 'Color\(0xFFFFFFFF\)', 'MaterialTheme.colorScheme.surface'
    $content = $content -replace 'Color\(0xFF292929\)', 'MaterialTheme.colorScheme.onSurface'
    $content = $content -replace 'Color\(0xFF2B7FFF\)', 'MaterialTheme.colorScheme.primary'
    $content = $content -replace 'Color\(0xFF0D0D0D\)', 'MaterialTheme.colorScheme.onSurface'
    
    if ($content -ne $originalContent -and $content -match 'MaterialTheme\.colorScheme' -and $content -notmatch 'import androidx\.compose\.material3\.MaterialTheme') {
        $content = $content -replace 'import androidx\.compose\.runtime\.Composable', "import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable"
    }

    if ($content -ne $originalContent) {
        Set-Content -Path $file.FullName -Value $content
    }
}
