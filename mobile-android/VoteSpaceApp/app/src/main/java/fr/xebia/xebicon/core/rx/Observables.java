package fr.xebia.xebicon.core.rx;

import java.util.List;

import rx.Observable;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.QueryResult;

public class Observables {

    public static <T extends QueryResult> Observable<List<T>> fromManyQuery(ManyQuery<T> query) {
        return Observable.create(subscriber -> {
            CursorList<T> cursorList = query.get();
            subscriber.onNext(cursorList.asList());
            subscriber.onCompleted();
        });
    }
}
