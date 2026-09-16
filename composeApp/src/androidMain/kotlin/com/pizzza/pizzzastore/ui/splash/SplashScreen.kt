package com.pizzza.pizzzastore.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pizzza.pizzzastore.R
import com.pizzza.pizzzastore.ui.AppViewModel
import com.valu.uitaycompose.swipe.UiTayGif
import com.valu.uitaycompose.utils.tay_grey_100
import com.valu.uitaycompose.utils.tay_grey_400
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textGabbiB35
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    viewModel: AppViewModel,
    onFinished: (Boolean) -> Unit
) {

    val scale = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        
        delay(500.milliseconds)
        viewModel.syncProducts(
            onComplete = { success ->
                    onFinished(success)

            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F5F4)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Image(painter = painterResource(R.drawable.ic_logo_store_new)
                , contentDescription = "null",modifier = Modifier.width(250.dp).height(250.dp))

            Spacer(Modifier.height(60.dp))

                CircularProgressIndicator(
                    color = tay_red_600,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "Cargando datos...",
                    color = tay_red_600.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
        }
    }
}
