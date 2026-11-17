package com.lokin.app.data.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateProvider @Inject constructor(private val auth: FirebaseAuth) {

    /** Phát uid mỗi khi đăng nhập/đăng xuất, null nếu chưa đăng nhập. */
    val uidFlow: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    val currentUid: String? get() = auth.currentUser?.uid
}