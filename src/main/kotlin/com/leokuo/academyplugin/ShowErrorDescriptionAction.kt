import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.nio.charset.StandardCharsets
import org.json.JSONObject

class ShowErrorDescriptionAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project: Project? = event.project
        val editor: Editor? = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)

        if (project == null || editor == null) return

        val selectionModel: SelectionModel = editor.selectionModel
        val selectedText: String? = selectionModel.selectedText

        if (selectedText.isNullOrBlank()) return

        val errorMap = loadErrorMessages(project)
        val errorMessage = errorMap[selectedText] ?: "找不到對應的錯誤描述。"

        showPopup(editor, errorMessage)
    }

    private fun loadErrorMessages(project: Project): Map<String, String> {
        val baseDir = project.basePath ?: return emptyMap()
        val errorFile: VirtualFile? = VfsUtil.findFileByIoFile(java.io.File("$baseDir/errors.json"), true)

        if (errorFile != null && errorFile.exists()) {
            val content = VfsUtil.loadText(errorFile)
            val jsonObject = JSONObject(content)
            return jsonObject.toMap().mapValues { it.value.toString() }
        }
        return emptyMap()
    }

    private fun showPopup(editor: Editor, message: String) {
        val popup = JBPopupFactory.getInstance()
            .createMessage(message)
        popup.showInBestPositionFor(editor)
    }
}
