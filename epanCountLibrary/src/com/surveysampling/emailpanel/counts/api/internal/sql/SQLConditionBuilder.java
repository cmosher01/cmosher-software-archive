/*
 * Created on June 1, 2005
 */
package com.surveysampling.emailpanel.counts.api.internal.sql;

/**
 * A simple helper class to build SQL condition clauses,
 * in the form: <code>[(term)[ AND \n(term)[...]]]</code>
 * Not thread-safe.
 * @author Chris Mosher
 */
public class SQLConditionBuilder
{
    private final StringBuffer appendTo;
    private final String conjunction;
    private boolean haveTerm;

    /**
     * Initializes this <code>SQLConditionBuilder</code>
     * to append built clauses to the given
     * <code>StringBuffer</code>.
     * @param appendTo
     * @param conjunction "AND" or "OR"
     */
    public SQLConditionBuilder(final StringBuffer appendTo, final String conjunction)
    {
        this.appendTo = appendTo;
        this.conjunction = conjunction.toUpperCase().trim();
        if (this.appendTo == null)
        {
            throw new NullPointerException();
        }
        if (this.conjunction.length() == 0)
        {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Appends the given term to the <code>StringBuffer</code>
     * passed in to the constructor, with "AND" if necessary.
     * @param termToAppend can be null or empty
     */
    public void appendTerm(final StringBuffer termToAppend)
    {
        if (termToAppend == null || termToAppend.length() == 0)
        {
            return;
        }

        if (this.haveTerm)
        {
            this.appendTo.append(" ");
            this.appendTo.append(this.conjunction);
            this.appendTo.append(" \n");
        }
        else
        {
            this.haveTerm = true;
        }

        this.appendTo.append("(");
        this.appendTo.append(termToAppend);
        this.appendTo.append(")");
    }
}
