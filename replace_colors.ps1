$files = @(
    "d:\downloads\Gemma\app\src\main\java\com\gemma\app\presentation\chat\components\MarkdownFormatter.kt",
    "d:\downloads\Gemma\app\src\main\java\com\gemma\app\presentation\chat\components\ChatTopBar.kt",
    "d:\downloads\Gemma\app\src\main\java\com\gemma\app\presentation\chat\components\ChatMessageList.kt",
    "d:\downloads\Gemma\app\src\main\java\com\gemma\app\presentation\chat\components\EmptyChatState.kt",
    "d:\downloads\Gemma\app\src\main\java\com\gemma\app\presentation\chat\components\ChatInputBar.kt",
    "d:\downloads\Gemma\app\src\main\java\com\gemma\app\presentation\chat\components\AttachmentMenu.kt",
    "d:\downloads\Gemma\app\src\main\java\com\gemma\app\presentation\chat\components\ChatDrawer.kt"
)

foreach ($file in $files) {
    $content = Get-Content $file -Raw
    $newContent = $content -replace 'Color\.White', 'MaterialTheme.colorScheme.surface'
    $newContent = $newContent -replace 'Color\.Black', 'MaterialTheme.colorScheme.onSurface'
    $newContent = $newContent -replace 'Color\.Gray', 'MaterialTheme.colorScheme.onSurfaceVariant'
    $newContent = $newContent -replace 'Color\.LightGray', 'MaterialTheme.colorScheme.surfaceVariant'
    $newContent = $newContent -replace 'Color\.DarkGray', 'MaterialTheme.colorScheme.onSurfaceVariant'
    
    if ($newContent -match 'MaterialTheme\.colorScheme' -and $newContent -notmatch 'import androidx\.compose\.material3\.MaterialTheme') {
        $newContent = $newContent -replace 'import androidx\.compose\.runtime\.Composable', "import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable"
    }

    Set-Content -Path $file -Value $newContent
}
