package net.questcraft.structure;

@FunctionalInterface
public interface ExceptionalConsumer<T> {
    void accept(T t) throws Exception;
}