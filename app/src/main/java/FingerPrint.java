//
//import android.content.Intent;
//import android.widget.Toast;
//
//import androidx.biometric.BiometricManager;
//import androidx.biometric.BiometricPrompt;
//import androidx.core.content.ContextCompat;
//import androidx.annotation.NonNull;
//
//import com.example.media_security.ContactsOption;
//import com.example.media_security.FingerPrintAuth;
//
//import java.util.concurrent.Executor;
//
//public class FingerPrint {
//
//    BiometricManager manager;
//    BiometricPrompt.PromptInfo promptInfo;
//    BiometricPrompt prompt;
//    Executor executor;
//
//    manager = BiometricManager.from(FingerPrintAuth.class);
//        switch (manager.canAuthenticate()) {
//        case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
//            Toast.makeText(FingerPrintAuth.this, " Biometric hardware currently unavailable", Toast.LENGTH_SHORT).show();
//            break;
//        case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
//            Toast.makeText(FingerPrintAuth.this, "No suitable hardware for biometric", Toast.LENGTH_SHORT).show();
//            break;
//        case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
//            Toast.makeText(FingerPrintAuth.this, "No biometric enrolled", Toast.LENGTH_SHORT).show();
//            break;
//        case BiometricManager.BIOMETRIC_SUCCESS:
//            break;
//    }
//
//    executor = ContextCompat.getMainExecutor(FingerPrintAuth.this);
//    // fragmentActivity: The activity of the client application that will host the prompt.
//    // executor : The executor that will be used to run BiometricPrompt.AuthenticationCallback methods.
//    // AuthenticationCallback: A collection of methods that may be invoked by BiometricPrompt during authentication.
//    prompt = new BiometricPrompt(FingerPrintAuth.this, executor, new BiometricPrompt.AuthenticationCallback() {
//        @Override
//        // user successfully authenticate
//        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
//            super.onAuthenticationSucceeded(result);
//            Toast.makeText(FingerPrintAuth.this, "Successfully authenticate ", Toast.LENGTH_SHORT).show();
//            Intent intent=new Intent(FingerPrintAuth.this , ContactsOption.class);
//            startActivity(intent);
//        }
//
//        // Called when an unrecoverable error has been encountered and authentication has stopped.
//        public void onAuthenticationError(int errcode, @NonNull CharSequence string) {
//            super.onAuthenticationError(errcode, string);
//        }
//
//        // called when biometric failed
//        public void onAuthenticationFailed() {
//            super.onAuthenticationFailed();
//        }
//
//    });
//
//    promptInfo = new BiometricPrompt.PromptInfo.Builder()
//            .setTitle("Biometric Authentication")
//                .setSubtitle("Login using finger Print")
//                .setNegativeButtonText("cancel")
//                .build();
//
//        prompt.authenticate(promptInfo);
//
//
//
//
//}
