package com.wilson.burbuja




object StoryMockProvider {

    fun obtenerCuentoSimulado(genero: String): String {
        return when (genero) {
            "Misterio" -> listOf(
                """
                La lente capturó algo que el ojo humano pasó por alto. Entre las sombras de la imagen, una silueta borrosa parecía observar desde un tiempo que ya no existe. No era solo una fotografía; era una ventana hacia una dimensión paralela. 
                
                Wilson sintió un escalofrío mientras el sensor procesaba la información. Cada píxel revelaba una verdad que durante décadas nadie se atrevió a nombrar. El ambiente se volvió pesado, y el silencio de la habitación solo era interrumpido por el latido acelerado de un descubrimiento que cambiaría el destino de todos los presentes.
                
                Al hacer zoom en la esquina superior izquierda, los reflejos en el vidrio Dreamline del fondo mostraban un código QR grabado en el aire. ¿Quién lo puso ahí? ¿Y por qué solo es visible a través de la IA de Burbuja? Las teorías empezaron a amontonarse en su mente. 
                
                A medida que la historia avanzaba, los fragmentos de realidad empezaron a encajar como piezas de un rompecabezas prohibido. La ciudad subterránea bajo sus pies no era un mito, y la entrada estaba mucho más cerca de lo que cualquiera hubiera imaginado jamás. La aventura apenas comenzaba, y el misterio se volvía más profundo con cada palabra escrita en la pantalla del dispositivo.
                """.trimIndent(),
                """
                Un reflejo extraño en el cristal reveló una habitación que no debería estar ahí. Al principio, parecía una simple falla técnica, un glitch en la matriz de procesamiento de la imagen, pero al observar con detenimiento, los detalles eran demasiado precisos para ser un error.
                
                Había muebles de otra época, libros con títulos en idiomas olvidados y una luz cenicienta que parecía devorar la realidad circundante. La IA analizó las partículas de polvo suspendidas en el aire de la foto, detectando una firma energética que no correspondía a este planeta.
                
                Wilson intentó cerrar la aplicación, pero la pantalla permanecía fija, mostrando cómo la silueta en el reflejo comenzaba a girar la cabeza lentamente hacia la cámara. No era una grabación del pasado; era una transmisión en vivo desde un lugar fuera del espacio-tiempo. 
                
                El corazón le latía con fuerza contra las costillas. Si esa figura lograba cruzar el umbral de los píxeles, ya no habría vuelta atrás. El misterio de la habitación oculta estaba a punto de desbordarse hacia el mundo real, y Burbuja era el único testigo de esta anomalía catastrófica.
                """.trimIndent()
            ).random()

            "Aventura" -> listOf(
                """
                ¡El horizonte nunca se vio tan cerca! Al capturar esta imagen, se activó un antiguo mecanismo de exploración que llevaba siglos dormido bajo las capas de la civilización moderna. Los colores vibrantes de la captura indicaban que el camino hacia lo desconocido estaba finalmente abierto para quien se atreviera a cruzarlo.
                
                Sin mapas físicos, guiados únicamente por la curiosidad y la potencia de la inteligencia artificial, nos lanzamos hacia un mundo donde las leyes de la física son solo sugerencias creativas. El viento soplaba con un aroma a sal y tecnología antigua, invitándonos a descubrir qué se ocultaba detrás de la siguiente montaña de datos.
                
                Cada paso en este nuevo territorio se sentía como una victoria sobre la rutina diaria. Encontramos ruinas de servidores gigantescos que alguna vez albergaron la memoria de toda una raza. Los cables, como raíces doradas, se extendían por el suelo transmitiendo pulsos de energía azulada que iluminaban nuestro camino.
                
                La expedición no estaba exenta de peligros. Tuvimos que saltar sobre abismos de código corrupto y esquivar centinelas mecánicos que aún custodiaban secretos de una era olvidada. Pero la recompensa valía el riesgo: en el corazón de la red, un santuario de luz nos esperaba con las respuestas a las preguntas más antiguas de la humanidad. ¡La verdadera aventura de Burbuja acababa de alcanzar su punto máximo!
                """.trimIndent()
            ).random()

            "Terror" -> listOf(
                """
                Hay fotos que nunca deberían ser tomadas, y momentos que es mejor dejar en el olvido. Al presionar el obturador, algo del otro lado aprovechó la luz del flash para cruzar a nuestro plano existencial, usando los circuitos del teléfono como un puente de carne y silicio.
                
                Ahora, la imagen en la pantalla parece moverse cuando no la miras directamente. Un susurro metálico emana de los altavoces, repitiendo palabras distorsionadas que te erizan la piel. El tono oscuro que elegiste en la configuración no fue una casualidad; fue una invitación inconsciente para que la entidad se manifestara.
                
                Notaste que, en el fondo de la imagen, las sombras han empezado a crecer de forma independiente a la fuente de luz. Se arrastran por las paredes de la habitación digital, buscando una salida hacia tu realidad física. El frío en la habitación es ahora insoportable, y puedes ver tu propio aliento congelándose en el aire.
                
                No mires atrás. Lo que sea que capturaste ya no está atrapado en la galería de fotos... está sentado justo detrás de ti, respirando al unísono con el procesador de tu celular. Burbuja ha abierto una puerta que no tiene cerrojo del lado de los vivos, y el precio por ver lo invisible podría ser mucho más alto de lo que estás dispuesto a pagar. La oscuridad no es solo la ausencia de luz, es una presencia que te ha elegido.
                """.trimIndent()
            ).random()

            else -> "Había una vez una imagen que guardaba un secreto increíble..."
        }
    }
}

