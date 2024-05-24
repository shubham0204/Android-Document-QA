import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

private val progressDialogVisibleState = mutableStateOf(false)

@Composable
fun AppProgressDialog() {
    val isVisible by remember { progressDialogVisibleState }
    if (isVisible) {
        Dialog(onDismissRequest = { /* Progress dialogs are non-cancellable */ }) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier.size(100.dp).background(Color.White, shape = RoundedCornerShape(8.dp))
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

fun showProgressDialog() {
    progressDialogVisibleState.value = true
}

fun hideProgressDialog() {
    progressDialogVisibleState.value = false
}
