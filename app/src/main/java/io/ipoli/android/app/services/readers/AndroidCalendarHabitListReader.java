package io.ipoli.android.app.services.readers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.ocpsoft.prettytime.shade.net.fortuna.ical4j.model.Dur;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.ipoli.android.Constants;
import io.ipoli.android.app.utils.LocalStorage;
import io.ipoli.android.quest.data.ExternalSourceMapping;
import io.ipoli.android.quest.data.Recurrence;
import io.ipoli.android.quest.data.RecurrentQuest;
import me.everything.providers.android.calendar.CalendarProvider;
import me.everything.providers.android.calendar.Event;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Venelin Valkov <venelin@curiousily.com>
 * on 5/11/16.
 */
public class AndroidCalendarHabitListReader implements ListReader<RecurrentQuest> {

    private final Set<String> habitIds;
    private final Context context;
    private final CalendarProvider calendarProvider;

    public AndroidCalendarHabitListReader(Context context, CalendarProvider calendarProvider, LocalStorage localStorage) {
        this.context = context;
        this.calendarProvider = calendarProvider;
        this.habitIds = localStorage.readStringSet(Constants.KEY_ANDROID_CALENDAR_HABITS_TO_UPDATE);
    }

    @Override
    public Observable<RecurrentQuest> read() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            return Observable.empty();
        }

        return Observable.just(habitIds).concatMapIterable(habitIds -> habitIds).concatMap(habitId -> {
            Event e = calendarProvider.getEvent(Integer.valueOf(habitId));
            RecurrentQuest habit = new RecurrentQuest("");
            habit.setName(e.title);
            habit.setSource("google-calendar");
            DateTime startDateTime = new DateTime(e.dTStart, DateTimeZone.forID(e.eventTimeZone));
            habit.setStartMinute(startDateTime.getMinuteOfDay());
            Dur dur = new Dur(e.duration);
            habit.setDuration((int) TimeUnit.MILLISECONDS.toMinutes(dur.getTime(new Date(0)).getTime()));
            Recurrence recurrence = new Recurrence();
            recurrence.setRrule(e.rRule);
            recurrence.setRdate(e.rDate);
            habit.setRecurrence(recurrence);
            habit.setExternalSourceMapping(ExternalSourceMapping.fromGoogleCalendar(e.id));
            return Observable.just(habit);
        }).compose(applyAPISchedulers());
    }

    private <T> Observable.Transformer<T, T> applyAPISchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
