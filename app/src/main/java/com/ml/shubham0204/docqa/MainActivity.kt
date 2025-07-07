package com.ml.shubham0204.docqa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ml.shubham0204.docqa.ui.screens.chat.ChatNavEvent
import com.ml.shubham0204.docqa.ui.screens.chat.ChatScreen
import com.ml.shubham0204.docqa.ui.screens.chat.ChatViewModel
import com.ml.shubham0204.docqa.ui.screens.docs.DocsScreen
import com.ml.shubham0204.docqa.ui.screens.docs.DocsViewModel
import com.ml.shubham0204.docqa.ui.screens.edit_api_key.EditAPIKeyScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
object ChatRoute

@Serializable
object EditAPIKeyRoute

@Serializable
object DocsRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()
            NavHost(
                navController = navHostController,
                startDestination = ChatRoute,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
            ) {
                composable<DocsRoute> { backStackEntry ->
                    val viewModel: DocsViewModel =
                        koinViewModel(viewModelStoreOwner = backStackEntry)
                    val docScreenUIState by viewModel.docsScreenUIState.collectAsState()
                    DocsScreen(
                        docScreenUIState,
                        onBackClick = { navHostController.navigateUp() },
                        onEvent = viewModel::onEvent,
                    )
                }
                composable<EditAPIKeyRoute> { EditAPIKeyScreen(onBackClick = { navHostController.navigateUp() }) }
                composable<ChatRoute> { backStackEntry ->
                    val viewModel: ChatViewModel =
                        koinViewModel(viewModelStoreOwner = backStackEntry)
                    val chatScreenUIState by viewModel.chatScreenUIState.collectAsState()
                    val navEvent by viewModel.navEventChannel.collectAsState(ChatNavEvent.None)
                    LaunchedEffect(navEvent) {
                        when (navEvent) {
                            is ChatNavEvent.ToDocsScreen -> {
                                navHostController.navigate(DocsRoute)
                            }

                            is ChatNavEvent.ToEditAPIKeyScreen -> {
                                navHostController.navigate(EditAPIKeyRoute)
                            }

                            is ChatNavEvent.None -> {}
                        }
                    }
                    ChatScreen(
                        screenUiState = chatScreenUIState,
                        onScreenEvent = { viewModel.onChatScreenEvent(it) },
                    )
                }
            }
        }
    }
}
