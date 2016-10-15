package cn.darkal.networkdiagnosis.Utils;

import android.content.Context;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadStatusDelegate;

/**
 * Created by xuzhou on 2016/8/16.
 */
public class FileUtils {

//    private UploadNotificationConfig getNotificationConfig(String filename) {
//        if (!displayNotification.isChecked()) return null;
//
//        return new UploadNotificationConfig().setIcon(R.drawable.ic_upload)
//                .setCompletedIcon(R.drawable.ic_upload_success)
//                .setErrorIcon(R.drawable.ic_upload_error)
//                .setTitle(filename)
//                .setInProgressMessage(getString(R.string.uploading))
//                .setCompletedMessage(getString(R.string.upload_success))
//                .setErrorMessage(getString(R.string.upload_error))
//                .setAutoClearOnSuccess(autoClearOnSuccess.isChecked())
//                .setClickIntent(new Intent(this, MainActivity.class))
//                .setClearOnAction(true)
//                .setRingToneEnabled(true);
//    }


    public static void uploadFiles(Context context, UploadStatusDelegate uploadStatusDelegate,String serverUrlString, String paramNameString, String filesToUploadString) {

//        final String filesToUploadString = filesToUpload.getText().toString();
        final String[] filesToUploadArray = filesToUploadString.split(",");

        for (String fileToUploadPath : filesToUploadArray) {
            try {
//                final String filename = getFilename(fileToUploadPath);

                MultipartUploadRequest req = new MultipartUploadRequest(context, serverUrlString)
                        .addFileToUpload(fileToUploadPath, paramNameString).setMethod("POST")

//                        .setNotificationConfig(getNotificationConfig(filename))
//                        .setCustomUserAgent(USER_AGENT)
//                        .setAutoDeleteFilesAfterSuccessfulUpload(autoDeleteUploadedFiles.isChecked())
//                        .setUsesFixedLengthStreamingMode(fixedLengthStreamingMode.isChecked())
                        .setMaxRetries(3);

//                if (useUtf8.isChecked()) {
//                    req.setUtf8Charset();
//                }

                req.setDelegate(uploadStatusDelegate).startUpload();



                // these are the different exceptions that may be thrown
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
