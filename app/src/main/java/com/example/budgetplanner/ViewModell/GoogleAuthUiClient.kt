package com.example.budgetplanner.auth // Or your chosen package

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.example.budgetplanner.R // Make sure to import your R class

// A data class to hold the user information we get from Google
data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)

class GoogleAuthUiClient(
    private val context: Context,
    private val credentialManager: CredentialManager =
        CredentialManager.create(context)
) {

    suspend fun signIn(): SignInResult {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(
                    context.getString(R.string.your_web_client_id)
                )
                .setFilterByAuthorizedAccounts(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = result.credential

            if (
                credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)

                SignInResult(
                    data = UserData(
                        userId = googleCredential.id,
                        username = googleCredential.displayName,
                        profilePictureUrl = googleCredential.profilePictureUri?.toString()
                    ),
                    errorMessage = null
                )
            } else {
                SignInResult(null, "Invalid credential")
            }

        } catch (e: Exception) {
            SignInResult(null, e.message)
        }
    }
}
