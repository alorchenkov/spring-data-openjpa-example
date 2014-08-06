package com.cpwr.gdo.simulator.web.test;

import java.util.List;

import org.apache.click.control.Column;
import org.apache.click.control.Table;
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.extras.control.TableInlinePaginator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.cpwr.gdo.simulator.model.FailedRequest;
import com.cpwr.gdo.simulator.service.SimulatorService;
import com.cpwr.gdo.simulator.web.page.BorderPage;

/**
 *
 */
@Component
public final class FailedRequests extends BorderPage {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(FailedRequests.class);

    @Autowired
    @Qualifier("simulatorService")
    private SimulatorService simulatorService;

    /** The table. */
    private Table table = new Table("failed-requests-table");

    @Override
    public String getHelpPageLink() {
        return "";
    }

    public FailedRequests() {
        addControl(table);

        // Setup failed requests table
        table.setClass(Table.CLASS_ITS);
        table.setPageSize(10);
        table.setShowBanner(true);
        table.setSortable(true);
        table.setPaginator(new TableInlinePaginator(table));
        table.setPaginatorAttachment(Table.PAGINATOR_INLINE);

        Column column = new Column("createdDate");
        column.setWidth("140px;");
        table.addColumn(column);

        column = new Column("requestType");
        column.setWidth("140px;");
        table.addColumn(column);

        column = new Column("request");
        column.setAutolink(true);
        column.setWidth("230px;");
        table.addColumn(column);

        // viewRulesLink.setImageSrc("/assets/images/table.png");
        // viewRulesLink.setTitle("View rules");

        column = new Column("comments");
        column.setTextAlign("center");
        // AbstractLink[] rulesLinks = new AbstractLink[] { viewRulesLink };
        // column.setDecorator(new LinkDecorator(table, rulesLinks,
        // "requestType"));
        column.setSortable(false);
        table.addColumn(column);

        table.setDataProvider(new DataProvider<FailedRequest>() {

            public List<FailedRequest> getData() {
                final List<FailedRequest> failedRequests;

                failedRequests = simulatorService.listFailedRequest();

                LOG.debug("Failed requests, amount: {}", failedRequests.size());

                return failedRequests;
            }
        });
    }
}
