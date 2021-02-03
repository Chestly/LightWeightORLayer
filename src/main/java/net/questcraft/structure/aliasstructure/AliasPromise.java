package net.questcraft.structure.aliasstructure;

import net.questcraft.stmt.metadata.features.AliasClauseValueFeature;

import java.util.Objects;

public class AliasPromise extends AliasClauseValueFeature {
    /**
     * -1 : Error
     * 0 : Pending
     * 1 : Success
     */
    private int state = 0;

    private AliasClauseValueFeature promise;

    public AliasPromise() {
    }

    public void fulfill(String value) {
        this.promise = new AliasClauseValueFeature(value);
        this.state = 1;
    }

    @Override
    public String parse() {
        assert state == 1  : "You must fulfill this promise to get back a result!";
        return promise.parse();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AliasPromise that = (AliasPromise) o;
        return state == that.state &&
                Objects.equals(promise, that.promise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), state, promise);
    }
}
