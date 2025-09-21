package com.tdcolvin.examplesharedobjecttransition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tdcolvin.examplesharedobjecttransition.ui.theme.ExampleSharedObjectTransitionTheme

// You need to enable the following experimental features in your build.gradle.kts (app)
// buildFeatures {
//     compose {
//         enableExperimentalFeatures = true
//     }
// }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExampleSharedObjectTransitionTheme {
                MyApp()
            }
        }
    }
}

data class Scarecrow(
    val id: Int,
    val name: String,
    val fact: String,
    val imageResId: Int
)

val scarecrows = listOf(
    Scarecrow(1, "Sir Reginald Squawkworth", "Sir Reginald insists on being called by his full title and is famously allergic to straw, a condition he finds deeply ironic.", R.drawable.scarecrow_1),
    Scarecrow(2, "Cornelius 'Corny' Cobbles", "Corny is a retired magician who specializes in making crows disappear, although his success rate is a subject of much debate among the local bird population.", R.drawable.scarecrow_2),
    Scarecrow(3, "Petunia Petalsworth", "Petunia is a freelance fashion consultant for barnyard animals and is currently working on a line of couture denim for goats.", R.drawable.scarecrow_3),
    Scarecrow(4, "Barnaby Button-Eyes", "Barnaby has a crippling fear of squirrels, which he claims have been plotting to steal his buttons since the dawn of time.", R.drawable.scarecrow_4),
    Scarecrow(5, "Ferdinand the Fearsome", "Despite his intimidating name, Ferdinand is actually a talented baker who dreams of one day opening a gluten-free bread shop for rodents.", R.drawable.scarecrow_5)
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = "scarecrowList"
        ) {
            composable("scarecrowList") {
                ScarecrowListScreen(
                    navController = navController,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
            // Change: The route now expects an integer ID
            composable(
                "scarecrowDetail/{scarecrowId}",
                arguments = listOf(navArgument("scarecrowId") { type = NavType.IntType }),
                enterTransition = { fadeIn(animationSpec = tween(500)) },
                exitTransition = { fadeOut(animationSpec = tween(500)) }
            ) { backStackEntry ->
                val scarecrowId = backStackEntry.arguments?.getInt("scarecrowId")
                ScarecrowDetailScreen(
                    scarecrowId = scarecrowId,
                    navController = navController,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ScarecrowListScreen(
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Choose your friendly scarecrow") }) }) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            items(scarecrows) { scarecrow ->
                ScarecrowItem(
                    scarecrow = scarecrow,
                    navController = navController,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ScarecrowItem(
    scarecrow: Scarecrow,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clickable {
                    navController.navigate("scarecrowDetail/${scarecrow.id}")
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp).fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = scarecrow.imageResId),
                    contentDescription = "Image of ${scarecrow.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .sharedElement(
                            rememberSharedContentState("scarecrowImage-${scarecrow.id}"),
                            animatedVisibilityScope = animatedVisibilityScope

                        )
                )
                Spacer(Modifier.height(8.dp))
                Text(text = scarecrow.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ScarecrowDetailScreen(
    scarecrowId: Int?,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        // Change: Find the scarecrow data based on the ID
        val scarecrow = remember(scarecrowId) { scarecrows.find { it.id == scarecrowId } }

        if (scarecrow == null) {
            // Handle case where scarecrow is not found, e.g., show an error or navigate back
            // This is a simple fallback for demonstration
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Scarecrow not found!")
            }
            return
        }

        Scaffold(topBar = {
            TopAppBar(
                title = { Text(scarecrow.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = scarecrow.imageResId),
                    contentDescription = "Scarecrow cover for ${scarecrow.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState("scarecrowImage-${scarecrow.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = scarecrow.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = scarecrow.fact,
                    fontSize = 18.sp
                )
                // Additional details can be added here
            }
        }
    }
}