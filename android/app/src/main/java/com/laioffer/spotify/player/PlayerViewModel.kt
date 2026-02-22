package com.laioffer.spotify.player

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.laioffer.spotify.datamodel.Album
import com.laioffer.spotify.datamodel.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(private val exoPlayer: ExoPlayer) : ViewModel(),
    Player.Listener {

    private val _uiState =
        MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        exoPlayer.addListener(this)
        viewModelScope.launch {
            flow {
                while (true) {
                    if (exoPlayer.isPlaying) {
                        emit(exoPlayer.currentPosition to exoPlayer.duration)
                    }
                    delay(1000)
                }
            }.collect {
                _uiState.value = uiState.value.copy(currentMs = it.first, durationMs = it.second)
                Log.d("SpotifyPlayer", "CurrentMs: ${it.first}, DurationMs: ${it.second}")
            }
        }

    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        Log.d("SpotifyPlayer", isPlaying.toString())
        _uiState.value = _uiState.value.copy(
            isPlaying = isPlaying
        )
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Log.d("SpotifyPlayer", error.toString())
    }

    override fun onCleared() {
        exoPlayer.removeListener(this)
        super.onCleared()
    }



    fun seekTo(positionMs: Long) {
        // optimistically update
        _uiState.value = uiState.value.copy(
            currentMs = positionMs
        )
        exoPlayer.seekTo(positionMs)
    }


    fun load(song: Song, album: Album) {

//        // Build the media item.
//        val mediaItem = MediaItem.fromUri(videoUri)
//// Set the media item to be played.
//        player.setMediaItem(mediaItem)
//// Prepare the player.
//        player.prepare()
//// Start the playback.
//        player.play()

        _uiState.value = PlayerUiState(album = album, song = song)
        val mediaItem = MediaItem.Builder().setUri(song.src).build() // MediaItem.fromUri
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    fun play() {
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun clear() {
        exoPlayer.pause()
        _uiState.value = PlayerUiState()
    }
}

data class PlayerUiState(
    val album: Album? = null,
    val song: Song? = null,
    val isPlaying: Boolean = false,
    val currentMs: Long = 0,
    val durationMs: Long = 0,
)
