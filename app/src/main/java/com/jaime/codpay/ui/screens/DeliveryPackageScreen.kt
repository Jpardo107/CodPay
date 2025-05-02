package com.jaime.codpay.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.jaime.codpay.ui.components.DeliveryPackageScreen.FinalComment
import com.jaime.codpay.ui.components.DeliveryPackageScreen.PhotoCapture
import com.jaime.codpay.ui.components.DeliveryPackageScreen.RecipientName
import com.jaime.codpay.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DeliveryPackageScreen(navController: NavController) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            count = 3,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> PhotoCapture(onCaptureClick = {/*TODO: Abrir camara y capturar imagen*/ })
                1 -> RecipientName(
                    nombreReceptor = "",
                    onNombreChange = {/*TODO: recepcionar nombre y guardar*/ })

                2 -> FinalComment(
                    comment = "",
                    onCommentChange = {/*TODO: recepcionar comentario y guardar*/ })
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage > 0) {
                Button(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color.Black)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Anterior"
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(120.dp))
            }
            if (pagerState.currentPage < 2) {
                Button(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color.Black)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Siguiente"
                    )
                }
            } else {
                Button(
                    onClick = {
                        navController.navigate(Screen.Home.route) // puedes cambiar esto por una pantalla de resumen o feedback
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) {
                    Text("Finalizar")
                }
            }
        }
    }

}