package es.usc.citius.servando.calendula.activities;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.util.List;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.persistence.Medicine;
import es.usc.citius.servando.calendula.persistence.Routine;
import es.usc.citius.servando.calendula.persistence.ScheduleItem;

/**
 * Helper class for showing and canceling message
 * notifications.
 * <p/>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class ReminderNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "Reminder";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p/>
     * TODO: Customize this method's arguments to present relevant content in
     * the notification.
     * <p/>
     * TODO: Customize the contents of this method to tweak the behavior and
     * presentation of message notifications. Make
     * sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     *
     * @see #cancel(Context)
     */
    public static void notify(final Context context,
                              final String exampleString, Routine r, List<ScheduleItem> doses, Intent intent) {
        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
        // TODO: Remove this if your notification has no relevant thumbnail.
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.ic_pill_48dp);
        final Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_pill_48dp);
        final String title = res.getString(R.string.message_notification_title_template, exampleString);

        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();

        for (ScheduleItem scheduleItem : doses) {

            Medicine med = scheduleItem.schedule().medicine();
            final SpannableStringBuilder SpItem = new SpannableStringBuilder();
            SpItem.append(med.name());
            SpItem.setSpan(new ForegroundColorSpan(Color.WHITE), 0, SpItem.length(), 0);
            SpItem.append("   " + scheduleItem.dose() + " " + med.presentation().units(context.getResources()));
            // add to style
            style.addLine(SpItem);
        }
        style.setBigContentTitle(title);
        style.setSummaryText(doses.size() + " meds to take now");


        final String ticker = exampleString;

        final String text = res.getString(
                R.string.message_notification_placeholder_text_template, exampleString);

        PendingIntent defaultIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final Intent delay = new Intent(context, StartActivity.class);
        delay.putExtra("action", StartActivity.ACTION_DELAY_ROUTINE);
        delay.putExtra("routine_id", r.getId());

        PendingIntent delayIntent = PendingIntent.getActivity(
                context,
                1,
                delay,
                PendingIntent.FLAG_UPDATE_CURRENT);


        final Intent cancel = new Intent(context, StartActivity.class);
        cancel.putExtra("action", StartActivity.ACTION_CANCEL_ROUTINE);
        cancel.putExtra("routine_id", r.getId());

        PendingIntent cancelIntent = PendingIntent.getActivity(
                context,
                2,
                cancel,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                        // Set required fields, including the small icon, the
                        // notification title, and text.
                .setSmallIcon(R.drawable.ic_pill_small)
                .setLargeIcon(picture)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                        // All fields below this line are optional.
                        // Use a default priority (recognized on devices running Android
                        // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                        // Provide a large icon, shown with the notification in the
                        // notification drawer on devices running Android 3.0 or later.
                .setLargeIcon(picture)

                        // Set ticker text (preview) information for this notification.
                .setTicker(ticker)

                        // Show a number. This is useful when stacking notifications of
                        // a single type.
                .setNumber(doses.size())

                        // If this notification relates to a past or upcoming event, you
                        // should set the relevant time information using the setWhen
                        // method below. If this call is omitted, the notification's
                        // timestamp will by set to the time at which it was shown.
                        // TODO: Call setWhen if this notification relates to a past or
                        // upcoming event. The sole argument to this method should be
                        // the notification timestamp in milliseconds.
                        //.setWhen(...)

                        // Set the pending intent to be initiated when the user touches
                        // the notification.
                .setContentIntent(defaultIntent)
                        //.setOngoing(true)
                        // add delay button
                .addAction(R.drawable.ic_history_white_24dp, "Delay 1 hour", delayIntent)
                .addAction(R.drawable.ic_alarm_off_white_24dp, "Cancel reminder", cancelIntent)
                        // Show an expanded list of items on devices running Android 4.1
                        // or later.
                .setStyle(style)

                        // Automatically dismiss the notification when it is touched.
                .setAutoCancel(false);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String, Routine, java.util.List, android.content.Intent)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}