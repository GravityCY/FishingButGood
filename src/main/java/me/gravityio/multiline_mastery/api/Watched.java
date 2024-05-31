package me.gravityio.multiline_mastery.api;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Watched<T> {

    public Set<Consumer<T>> watchers = new HashSet<>();

    private T t;

    public Watched(T t) {
        this.t = t;
    }

    public void changed(Consumer<T> changed) {
        if (!this.watchers.remove(changed)) {
            this.watchers.add(changed);
        }
    }

    public void set(T v) {
        this.t = v;
        for (Consumer<T> watcher : this.watchers) {
            watcher.accept(this.t);
        }
    }

    public void setInternal(T v) {
        this.t = v;
    }

    public T get() {
        return this.t;
    }

    public static class Adapter<T> extends TypeAdapter<Watched<T>> {

        private final TypeAdapter<T> value;

        public Adapter(TypeAdapter<T> value) {
            this.value = value;
        }

        @Override
        public void write(JsonWriter out, Watched<T> watched) throws IOException {
            if (watched == null) {
                return;
            }
            out.value((int) watched.get());
        }

        @Override
        public Watched<T> read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            return new Watched<>(this.value.read(in));
        }
    }
}
