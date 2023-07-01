package org.listenbrainz.android.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import org.listenbrainz.android.R

class TriangleShape : Shape {
    override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)//Top Right corner
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, 300f)
            close()
        }
        return Outline.Generic(path)
    }
}
@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun ShimmerAnimationBox() {
    val ShimmerColorShades = listOf(
            Color.LightGray.copy(0.9f),
            Color.LightGray.copy(0.2f),
            Color.LightGray.copy(0.9f)
    )
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(


                    // Tween Animates between values over specified [durationMillis]
                    tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                    RepeatMode.Reverse
            )
    )

    val brush = Brush.linearGradient(
            colors = ShimmerColorShades,
            start = Offset(10f, 10f),
            end = Offset(translateAnim, translateAnim)
    )
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(TriangleShape())
                        .background(brush = brush),
        ) {
            // Content inside the box
        }
        Box(
                modifier = Modifier
                        .width(330.dp)
                        .height(200.dp)
                        .zIndex(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .align(Alignment.BottomCenter)
                        .background(brush=brush)
        ) {
        }
    }
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun ShimmerAnimation() {
    val ShimmerColorShades = listOf(
            Color.LightGray.copy(0.9f),
            Color.LightGray.copy(0.2f),
            Color.LightGray.copy(0.9f)
    )
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(


                    // Tween Animates between values over specified [durationMillis]
                    tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                    RepeatMode.Reverse
            )
    )

    val brush = Brush.linearGradient(
            colors = ShimmerColorShades,
            start = Offset(10f, 10f),
            end = Offset(translateAnim, translateAnim)
    )
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    Surface(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            shape = RoundedCornerShape(5.dp),
            shadowElevation = 5.dp
    ) {
        Row(
                modifier = Modifier
                        .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                    modifier = Modifier.size(70.dp)
                            .padding(10.dp)
                            .background(brush = brush),
            ) {
            }
            Box(modifier = Modifier
                    .width(screenWidth-70.dp)
                    .height(70.dp)
                    .padding(10.dp)
                    .background(brush = brush)
            )
            {

            }
        }
    }
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun ShimmerAnimationBigBox() {
    val ShimmerColorShades = listOf(
            Color.LightGray.copy(0.9f),
            Color.LightGray.copy(0.2f),
            Color.LightGray.copy(0.9f)
    )
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(


                    // Tween Animates between values over specified [durationMillis]
                    tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                    RepeatMode.Reverse
            )
    )

    val brush = Brush.linearGradient(
            colors = ShimmerColorShades,
            start = Offset(10f, 10f),
            end = Offset(translateAnim, translateAnim)
    )
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    Box(
            modifier = Modifier
                    .padding(2.dp)
                    .padding(top = 5.dp, bottom = 5.dp, start = 2.dp, end = 2.dp)
    ) {
        Row {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                        modifier = Modifier
                                .padding(2.dp)
                                .size(130.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(brush = brush)
                ) {

                }
                Box(
                        modifier = Modifier
                                .width(130.dp)
                                .height(20.dp)
                                .padding(2.dp)
                                .background(brush = brush)
                )
                Box(
                        modifier = Modifier
                                .width(130.dp)
                                .height(20.dp)
                                .padding(2.dp)
                                .background(brush = brush)
                )
            }
        }
    }
}