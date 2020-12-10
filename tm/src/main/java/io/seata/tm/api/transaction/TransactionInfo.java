package io.seata.tm.api.transaction;

import io.seata.common.util.CollectionUtils;

import java.io.Serializable;
import java.util.Set;

/**
 * @author guoyao
 */
public final class TransactionInfo implements Serializable {

    public static final int DEFAULT_TIME_OUT = 60000;

    private int timeOut;

    private String name;

    private Set<RollbackRule> rollbackRules;

    private Propagation propagation;

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RollbackRule> getRollbackRules() {
        return rollbackRules;
    }

    public void setRollbackRules(Set<RollbackRule> rollbackRules) {
        this.rollbackRules = rollbackRules;
    }

    public boolean rollbackOn(Throwable ex) {

        RollbackRule winner = null;
        int deepest = Integer.MAX_VALUE;

        if (CollectionUtils.isNotEmpty(rollbackRules)) {
            winner = NoRollbackRule.DEFAULT_NO_ROLLBACK_RULE;
            for (RollbackRule rule : this.rollbackRules) {
                int depth = rule.getDepth(ex);
                if (depth >= 0 && depth < deepest) {
                    deepest = depth;
                    winner = rule;
                }
            }
        }

        return !(winner instanceof NoRollbackRule);
    }

    public Propagation getPropagation() {
        if (this.propagation != null) {
            return this.propagation;
        }
        //default propagation
        return Propagation.REQUIRED;
    }

    public void setPropagation(Propagation propagation) {
        this.propagation = propagation;
    }
}
