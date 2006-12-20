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
package org.talend.designer.core.ui.editor.properties;

import java.util.Iterator;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.tabbed.ITypeMapper;
import org.talend.commons.ui.image.EImage;
import org.talend.commons.ui.image.ImageProvider;
import org.talend.core.ui.images.ECoreImage;
import org.talend.designer.core.ui.editor.connections.ConnLabelEditPart;
import org.talend.designer.core.ui.editor.connections.Connection;
import org.talend.designer.core.ui.editor.connections.ConnectionLabel;
import org.talend.designer.core.ui.editor.connections.ConnectionPart;
import org.talend.designer.core.ui.editor.nodecontainer.NodeContainerPart;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.nodes.NodeLabelEditPart;
import org.talend.designer.core.ui.editor.nodes.NodePart;
import org.talend.designer.core.ui.editor.outline.NodeReturnsTreeEditPart;
import org.talend.designer.core.ui.editor.outline.NodeTreeEditPart;
import org.talend.designer.core.ui.editor.outline.ProcessTreeEditPart;
import org.talend.designer.core.ui.editor.process.Process;
import org.talend.designer.core.ui.editor.process.ProcessPart;

/**
 * Label provider for the title bar for the tabbed property sheet page. <br/>
 * 
 * $Id$
 * 
 */
public class GefEditorLabelProvider extends LabelProvider {

    private ITypeMapper typeMapper;

    private Node lastNode = null;

    public GefEditorLabelProvider() {
        super();
        typeMapper = new GefEditorTypeMapper();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    public Image getImage(Object objects) {
        Node node;
        if (objects == null || objects.equals(StructuredSelection.EMPTY)) {
            return null;
        }
        if (!(objects instanceof IStructuredSelection)) {
            return null;
        }
        final boolean[] multiple = { false };
        Object object = getObject(objects, multiple);
        if (object == null) {
            return null;
        }

        if ((object instanceof NodeTreeEditPart)) {
            node = (Node) ((NodeTreeEditPart) object).getModel();
        } else {
            if (object instanceof NodeReturnsTreeEditPart) {
                node = lastNode;
            } else {
                if (object instanceof ProcessPart) {
                    return ImageProvider.getImage(ECoreImage.PROCESS_ICON);
                }
                if (object instanceof ConnectionPart) {
                    return ImageProvider.getImage(EImage.RIGHT_ICON);
                }
                if (object instanceof ConnLabelEditPart) {
                    return ImageProvider.getImage(EImage.RIGHT_ICON);
                }
                if ((object instanceof NodeLabelEditPart)) {
                    object = ((NodeLabelEditPart) object).getNodePart();
                }
                if (!(object instanceof NodePart)) {
                    return null;
                }
                node = (Node) ((NodePart) object).getModel();
            }
        }
        if (lastNode != node) {
            lastNode = node;
        }
        return node.getIcon24().createImage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText(Object objects) {
        Node node;
        if (objects == null || objects.equals(StructuredSelection.EMPTY)) {
            return "No items selected";
        }
        if (!(objects instanceof IStructuredSelection)) {
            return null;
        }
        final boolean[] multiple = { false };
        Object object = getObject(objects, multiple);
        if (object == null/* || ((IStructuredSelection) objects).size() > 1 */) {
            return "No items selected";
        } else {
            if (object instanceof NodeContainerPart) {
                NodeContainerPart nContainer = (NodeContainerPart) object;
                Process process = (Process) nContainer.getParent().getModel();
                return process.getName();
            } else if (object instanceof ProcessPart) {
                Process process = (Process) ((ProcessPart) object).getModel();
                return process.getLabel();
            } else if (object instanceof ProcessTreeEditPart) {
                Process process = (Process) ((ProcessTreeEditPart) object).getModel();
                return process.getName();
            }
            if (object instanceof ConnectionPart) {
                Connection conn = (Connection) ((ConnectionPart) object).getModel();
                return conn.getName();
            }
            if (object instanceof ConnLabelEditPart) {
                Connection conn = (Connection) ((ConnectionLabel) ((ConnLabelEditPart) object).getModel())
                        .getConnection();
                return conn.getName();
            }
            if (object instanceof NodeTreeEditPart) {
                node = (Node) ((NodeTreeEditPart) object).getModel();
            } else {
                if (object instanceof NodeReturnsTreeEditPart) {
                    node = lastNode;
                } else {
                    if (object instanceof NodeLabelEditPart) {
                        object = ((NodeLabelEditPart) object).getNodePart();
                    }
                    if (!(object instanceof NodePart)) {
                        return null;
                    }
                    node = (Node) ((NodePart) object).getModel();
                }
            }
            if (lastNode != node) {
                lastNode = node;
            }
            String name = node.getComponent().getTranslatedName();
            if (!node.getComponent().getTranslatedName().equals(node.getComponent().getName())) {
                name += " (" + node.getComponent().getName() + ")";
            }
            return name;
        }
    }

    /**
     * Determine if a multiple object selection has been passed to the label provider. If the objects is a
     * IStructuredSelection, see if all the objects in the selection are the same and if so, we want to provide labels
     * for the common selected element.
     * 
     * @param objects a single object or a IStructuredSelection.
     * @param multiple first element in the array is true if there is multiple unequal selected elements in a
     * IStructuredSelection.
     * @return the object to get labels for.
     */
    private Object getObject(Object objects, boolean[] multiple) {
        Assert.isNotNull(objects);
        Object object = null;
        if (objects instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) objects;
            object = selection.getFirstElement();
            if (selection.size() == 1) {
                // one element selected
                multiple[0] = false;
                return object;
            }
            // multiple elements selected
            multiple[0] = true;
            Class firstClass = typeMapper.mapType(object);
            // determine if all the objects in the selection are the same type
            if (selection.size() > 1) {
                for (Iterator i = selection.iterator(); i.hasNext();) {
                    Object next = i.next();
                    Class nextClass = typeMapper.mapType(next);
                    if (!nextClass.equals(firstClass)) {
                        multiple[0] = false;
                        object = null;
                        break;
                    }
                }
            }
        } else {
            multiple[0] = false;
            object = objects;
        }
        return object;
    }

}
