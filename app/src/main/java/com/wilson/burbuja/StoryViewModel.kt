package com.wilson.burbuja

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.wilson.burbuja.data.ElevenLabsRepository
import com.wilson.burbuja.data.GeminiService
import java.io.File
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class StoryViewModel : ViewModel() {

    private val geminiService = GeminiService()
    private var audioRepository: ElevenLabsRepository? = null
    private var mediaPlayer: MediaPlayer? = null

    private var audioManager: AudioManager? = null
    private var focusRequest: AudioFocusRequest? = null

    // --- Controlador de Ondas para la UI ---
    var audioAmplitude by mutableStateOf(0f)
        private set
    private var visualizerJob: Job? = null

    var uiState: StoryState by mutableStateOf(StoryState.Idle)
        private set

    var audioFile: File? by mutableStateOf(null)
        private set

    var isAudioLoading by mutableStateOf(false)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var audioErrorMessage: String? by mutableStateOf(null)
        private set

    fun clearAudioError() {
        audioErrorMessage = null
    }

    fun generarHistoria(context: Context, data: StoryData) {
        viewModelScope.launch {
            uiState = StoryState.Loading
            try {
                val bitmap = obtenerBitmap(context, data.photoUri)
                if (bitmap != null) {
                    val resultado = geminiService.generarCuentoMultimodal(data, bitmap)
                    if (resultado != null) {
                        uiState = StoryState.Success(
                            title = resultado.first,
                            story = resultado.second
                        )
                        audioFile = null
                        detenerAudio()
                    } else {
                        uiState = StoryState.Error("Gemini no pudo crear el cuento.")
                    }
                } else {
                    uiState = StoryState.Error("No pudimos leer la imagen de la burbuja.")
                }
            } catch (e: Exception) {
                uiState = StoryState.Error("Error inesperado: ${e.message}")
            }
        }
    }

    // --- SIMULACIÓN RESTAURADA PARA AHORRAR CRÉDITOS ---
    fun prepararAudio(context: Context, texto: String, voiceId: String, storyId: String) {
        viewModelScope.launch {
            isAudioLoading = true
            audioErrorMessage = null

            println("Burbuja Debug: Iniciando simulación de audio...")

            delay(2000) // Simulamos ElevenLabs procesando

            isAudioLoading = false
            isPlaying = true
            startVisualizerLoop() // Arrancamos la onda

            println("Burbuja Debug: Simulación lista. ¡Mirá el orbe!")

            // Cuando quieras volver a la API real, borrá lo de arriba y descomentá esto:
            // val fileName = "story_audio_$storyId.mp3"
            // descargarAudio(context, texto, voiceId, fileName)
        }
    }

    private suspend fun descargarAudio(context: Context, texto: String, voiceId: String, fileName: String) {
        if (audioRepository == null) {
            audioRepository = ElevenLabsRepository(context)
        }
        try {
            val archivo = audioRepository?.obtenerRelatoAudio(texto, voiceId, fileName)
            if (archivo != null) {
                audioFile = archivo
                isAudioLoading = false
                alternarAudio(context, archivo)
            } else {
                isAudioLoading = false
                audioErrorMessage = "No se pudo procesar el archivo de audio."
            }
        } catch (e: Exception) {
            if (e.message == "SIN_CREDITOS") {
                audioErrorMessage = "¡Ups! Nos quedamos sin créditos de voz en ElevenLabs."
            } else {
                audioErrorMessage = "Hubo un problema al conectar con el narrador."
            }
            isAudioLoading = false
        }
    }

    fun alternarAudio(context: Context, file: File) {
        configurarAudioFocus(context)

        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isPlaying = false
            stopVisualizerLoop()
            audioManager?.abandonAudioFocusRequest(focusRequest!!)
            return
        }

        if (mediaPlayer != null && !isPlaying) {
            val res = audioManager?.requestAudioFocus(focusRequest!!)
            if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer?.start()
                isPlaying = true
                startVisualizerLoop()
            }
            return
        }

        val res = audioManager?.requestAudioFocus(focusRequest!!)
        if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer = MediaPlayer().apply {
                try {
                    setDataSource(file.absolutePath)
                    prepare()
                    start()
                    this@StoryViewModel.isPlaying = true
                    startVisualizerLoop()

                    setOnCompletionListener {
                        this@StoryViewModel.isPlaying = false
                        stopVisualizerLoop()
                        audioManager?.abandonAudioFocusRequest(focusRequest!!)
                        it.release()
                        mediaPlayer = null
                    }
                } catch (e: Exception) {
                    this@StoryViewModel.isPlaying = false
                    stopVisualizerLoop()
                }
            }
        }
    }

    // --- Rutina matemática que simula el ecualizador de voz ---
    private fun startVisualizerLoop() {
        visualizerJob?.cancel()
        visualizerJob = viewModelScope.launch {
            while (isPlaying) {
                // Genera el patrón: 15% del tiempo hace pausas naturales, 85% son frecuencias de voz
                val isPause = Random.nextFloat() > 0.85f
                val targetAmplitude = if (isPause) {
                    Random.nextFloat() * 0.15f // Susurros o pausas
                } else {
                    0.3f + (Random.nextFloat() * 0.7f) // Picos de voz
                }

                audioAmplitude = targetAmplitude
                delay((50..150).random().toLong()) // Ritmo irregular de actualización
            }
        }
    }

    private fun stopVisualizerLoop() {
        visualizerJob?.cancel()
        audioAmplitude = 0f // Reseteamos la cápsula al apagar
    }

    private fun configurarAudioFocus(context: Context) {
        if (audioManager == null) {
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            val focusAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(focusAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener { focusChange ->
                    when (focusChange) {
                        AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                            if (mediaPlayer?.isPlaying == true) {
                                mediaPlayer?.pause()
                                isPlaying = false
                                stopVisualizerLoop()
                            }
                        }
                        AudioManager.AUDIOFOCUS_GAIN -> {}
                    }
                }
                .build()
        }
    }

    fun detenerAudio() {
        isPlaying = false
        isAudioLoading = false
        stopVisualizerLoop()

        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
        focusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
    }

    override fun onCleared() {
        super.onCleared()
        detenerAudio()
    }

    private suspend fun obtenerBitmap(context: Context, uri: String): Bitmap? {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .allowHardware(false)
            .build()

        val result = loader.execute(request)
        return if (result is SuccessResult) {
            (result.drawable as BitmapDrawable).bitmap
        } else {
            null
        }
    }
}