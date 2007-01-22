// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006 Talend - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.sqlbuilder.erdiagram.ui.nodes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: Table.java 1 2006-12-25 下午02:55:50 +0000 (ææäº, 29 ä¹æ 2006) yzhang $
 * 
 */
public class Table extends Element {

    public static final String PROP_ERDIAGRAM = "erDiagram";

    public static final String PROP_SIZE = "size";

    public static final String PROP_NAME = "name";

    public static final String PROP_COLUMNS = "columns";

    public static final String PROP_LOCATION = "location";

    private static final long serialVersionUID = 1L;

    private MetadataTable metadataTable;

    private List columns;

    private ErDiagram erDiagram;

    private Point location = new Point(10, 10);

    private Dimension size = new Dimension(0, 20);

    private int maxHeight = 0;

    private int maxWidth = 0;

    public List getColumns() {
        return this.columns;
    }

    public Table() {
        columns = new ArrayList();
    }

    /**
     * Form the columns within table depends on the metadata columns in metadata table.
     * 
     * DOC yzhang Comment method "formColumns".
     */
    private void formColumns() {
        EList metadataColumns = metadataTable.getColumns();
        Column col = new Column();
        col.setElementName("*");
        col.setSelected(true);
        col.setTable(this);
        addColumn(col);
        Iterator iterator = metadataColumns.iterator();
        while (iterator.hasNext()) {
            MetadataColumn metadataColumn = (MetadataColumn) iterator.next();
            Column column = new Column();
            column.setMetadataColumn(metadataColumn);
            column.setSelected(true);
            column.setTable(this);
            addColumn(column);
        }

    }

    /**
     * DOC yzhang Comment method "addColumn".
     * 
     * @param column
     */
    @SuppressWarnings("unchecked")
    private void addColumn(Object column) {
        columns.add(column);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.sqlbuider.erdiagram.model.Element#getElementName()
     */
    @Override
    public String getElementName() {
        return metadataTable.getSourceName();
    }

    public void setMetadataTable(MetadataTable metadataTable) {
        this.metadataTable = metadataTable;
        formColumns();
        size.height = 17;
        size.width = this.metadataTable.getSourceName().length() * 7 + 6;
        for (Object object : columns) {
            if (object instanceof Column) {
                Dimension dimension = ((Column) object).getSize();
                size.height += dimension.height;
                if (dimension.width > size.width) {
                    size.width = dimension.width;
                }
            }
        }
        maxHeight = size.height;
        maxWidth = size.width;
        if (maxHeight > 220) {
            size.height = 220;
        }

        fireStructureChange(PROP_COLUMNS, this.columns);
    }

    public ErDiagram getErDiagram() {
        return erDiagram;
    }

    public void setErDiagram(ErDiagram erDiagram) {
        this.erDiagram = erDiagram;
        if (this.erDiagram != null && !this.erDiagram.getTables().isEmpty()) {
            Table lastTable = this.erDiagram.getTables().get(this.erDiagram.getTables().size() - 1);
            int x = lastTable.getLocation().x + lastTable.getSize().width + 30;
            int y = lastTable.getLocation().y;
            if (x > 600) {
                // int mxH = 0;
                // for (Object obj : this.erDiagram.getTables()) {
                // if (obj instanceof Table) {
                // if (mxH < ((Table) obj).getSize().height) {
                // mxH = ((Table) obj).getSize().height;
                // }
                // }
                // }
                // if (mxH < 250) {
                // y = y + mxH + 30;
                // } else {
                y = y + 250;
                // }
                x = 10;
            }
            this.location.x = x;
            this.location.y = y;
        }

        fireStructureChange(PROP_ERDIAGRAM, this.erDiagram);
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
        fireStructureChange(PROP_LOCATION, this.location);
    }

    public void setLocation(int x, int y) {
        this.location.setLocation(x, y);
        fireStructureChange(PROP_LOCATION, this.location);
    }

    public Dimension getSize() {
        return this.size;
    }

    public void setSize(Dimension size) {
        this.size = size;
        if (size.height > maxHeight) {
            this.size.height = maxHeight;
        }
        if (size.width < maxWidth) {
            this.size.width = maxWidth;
        }
        fireStructureChange(PROP_SIZE, this.size);
    }
}
