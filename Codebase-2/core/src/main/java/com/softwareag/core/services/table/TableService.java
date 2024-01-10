package com.softwareag.core.services.table;

import org.apache.sling.api.resource.PersistenceException;

import javax.jcr.RepositoryException;

/**
 * Interface for Table Component, Table component offers servlet for multiple actions with different selectors and which in turn called handleAction of TableService which handles multipole actions
 *
 * @author : Yunus Bayraktar (bayrakta@adobe.com)
 */
public interface TableService {

    /**
     * Table component offers servlet for multiple actions with different selectors and which in turn called handleAction of TableService
     */
    void handleAction(TableActionResult actionResult, TableAction action);

    /**
     * addLeftColumn adds the column to the left of the table
     */

    void addLeftColumn(TableActionResult actionResult, TableAction action)
        throws PersistenceException, RepositoryException;

    /**
     * addRightColumn adds the column to the right of the table
     */
    void addRightColumn(TableActionResult actionResult, TableAction action)
        throws PersistenceException, RepositoryException;

    /**
     * deleteColumn deletes the current column of the selected cell
     */
    void deleteColumn(TableActionResult actionResult, TableAction action)
        throws PersistenceException;

    /**
     * addRowAbove adds the row above the selected cell except head cell.
     */
    void addRowAbove(TableActionResult actionResult, TableAction action)
        throws RepositoryException, PersistenceException;

    /**
     * addRowBelow adds the row below the selected cell.
     */
    void addRowBelow(TableActionResult actionResult, TableAction action)
        throws RepositoryException, PersistenceException;

    /**
     * deleteRow deletes the row of the selected cell.
     */
    void deleteRow(TableActionResult actionResult, TableAction action)
        throws PersistenceException;

    void mergeWithRight(TableActionResult actionResult, TableAction action);

    void mergeWithBelow(TableActionResult actionResult, TableAction action);

    void splitHorizontally(TableActionResult actionResult, TableAction action);

    void splitVertically(TableActionResult actionResult, TableAction action);

}
