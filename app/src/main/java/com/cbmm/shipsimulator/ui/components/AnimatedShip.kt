package com.cbmm.shipsimulator.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cbmm.shipsimulator.R
import com.cbmm.shipsimulator.data.model.Location
import com.cbmm.shipsimulator.util.MapUtils
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun AnimatedShip(
    currentLocation: Location,
    targetLocation: Location?,
    speed: Double, // in knots
    heading: Double, // in degrees
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val density = LocalDensity.current
    var currentPosition by remember { mutableStateOf(currentLocation) }
    var rotation by remember { mutableStateOf(heading) }
    
    // Anima a posição do navio
    val animatedPosition by animateValueAsState(
        targetValue = currentPosition,
        typeConverter = Location.VectorConverter,
        animationSpec = tween(
            durationMillis = 1000, // 1 segundo para atualização suave
            easing = LinearEasing
        )
    )
    
    // Anima a rotação do navio
    val animatedRotation by animateFloatAsState(
        targetValue = rotation.toFloat(),
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        )
    )
    
    // Efeito para atualizar a posição quando as coordenadas mudarem
    LaunchedEffect(currentLocation, targetLocation, speed) {
        if (targetLocation != null) {
            // Calcula a distância até o destino
            val distance = MapUtils.calculateDistance(
                currentLocation.latitude,
                currentLocation.longitude,
                targetLocation.latitude,
                targetLocation.longitude
            )
            
            // Tempo estimado para chegar ao destino (em ms)
            val duration = (distance / (speed * 0.514)).toLong() * 1000 // knots to m/s
            
            // Atualiza a posição atual para iniciar a animação
            currentPosition = currentLocation
            
            // Calcula o ângulo entre a posição atual e o destino
            rotation = MapUtils.calculateBearing(
                currentLocation.latitude,
                currentLocation.longitude,
                targetLocation.latitude,
                targetLocation.longitude
            ).toDouble()
            
            // Aguarda um pouco antes de atualizar para a próxima posição
            delay(1000)
        } else {
            // Se não houver destino, apenas atualiza a posição atual
            currentPosition = currentLocation
        }
    }
    
    // Desenha o navio no mapa
    Image(
        painter = painterResource(id = R.drawable.ic_ship),
        contentDescription = "Ship",
        modifier = Modifier
            .offset(
                x = with(density) { animatedPosition.x.dp.toPx() }.toDp(),
                y = with(density) { animatedPosition.y.dp.toPx().toDp() }
            )
            .graphicsLayer {
                rotationZ = animatedRotation
                // Adiciona um efeito de destaque se o navio estiver selecionado
                if (isSelected) {
                    scaleX = 1.3f
                    scaleY = 1.3f
                }
            }
            .then(if (isSelected) Modifier.shadow(4.dp) else Modifier)
    )
}

// Função de extensão para converter Float para Dp
private fun Float.toDp(): Dp = (this / LocalDensity.current.density).dp

// Classe utilitária para animação de Location
object Location {
    object VectorConverter : TwoWayConverter<Location, AnimationVector2D> {
        override val convertToVector: (Location) -> AnimationVector2D = { location ->
            AnimationVector2D(
                location.latitude.toFloat(),
                location.longitude.toFloat()
            )
        }
        
        override val convertFromVector: (AnimationVector2D) -> Location = { vector ->
            Location(vector.v1.toDouble(), vector.v2.toDouble())
        }
    }
}
