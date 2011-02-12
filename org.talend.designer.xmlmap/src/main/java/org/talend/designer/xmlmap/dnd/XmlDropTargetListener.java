// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.xmlmap.dnd;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.talend.designer.xmlmap.editor.XmlMapGraphicViewer;
import org.talend.designer.xmlmap.figures.ExpressionFigure;
import org.talend.designer.xmlmap.model.emf.xmlmap.InputXmlTree;
import org.talend.designer.xmlmap.model.emf.xmlmap.NodeType;
import org.talend.designer.xmlmap.model.emf.xmlmap.OutputTreeNode;
import org.talend.designer.xmlmap.model.emf.xmlmap.TreeNode;
import org.talend.designer.xmlmap.parts.OutputTreeNodeEditPart;
import org.talend.designer.xmlmap.parts.TreeNodeEditPart;
import org.talend.designer.xmlmap.parts.VarNodeEditPart;
import org.talend.designer.xmlmap.util.XmlMapUtil;

/**
 * wchen class global comment. Detailled comment
 */
public class XmlDropTargetListener extends TemplateTransferDropTargetListener {

    private IFigure targetFigure;

    public XmlDropTargetListener(EditPartViewer viewer) {
        super(viewer);
    }

    @Override
    protected void updateTargetRequest() {
        ((CreateRequest) getTargetRequest()).setLocation(getDropLocation());
    }

    @Override
    protected Request createTargetRequest() {
        CreateNodeConnectionRequest request = new CreateNodeConnectionRequest(getTargetEditPart());
        if (targetFigure instanceof ExpressionFigure) {
            request.setDropType(CreateNodeConnectionRequest.DROP_EXPRESSION);
        }
        request.setFactory(new NewNodeCreationFactory(TemplateTransfer.getInstance().getObject()));
        return request;
    }

    @Override
    protected void updateTargetEditPart() {
        super.updateTargetEditPart();
        if (getViewer() instanceof XmlMapGraphicViewer) {
            Point dropLocation = getDropLocation();
            EditPartViewer.Conditional condition = new EditPartViewer.Conditional() {

                public boolean evaluate(EditPart editpart) {
                    return editpart.getTargetEditPart(getTargetRequest()) != null;
                }
            };
            targetFigure = ((XmlMapGraphicViewer) getViewer()).findFigureAt(dropLocation.x, dropLocation.y, getExclusionSet(),
                    condition);
        }
    }

    public void dragEnter(DropTargetEvent event) {
    }

    public void dragLeave(DropTargetEvent event) {

    }

    public void dragOperationChanged(DropTargetEvent event) {

    }

    @Override
    public void dragOver(DropTargetEvent event) {
        super.dragOver(event);
        Object transferedObj = TemplateTransfer.getInstance().getObject();
        if (transferedObj == null) {
            event.detail = DND.DROP_NONE;
        } else {
            boolean isLookup = false;
            if (getTargetEditPart() instanceof TreeNodeEditPart && !(getTargetEditPart() instanceof OutputTreeNodeEditPart)) {
                TreeNode inputTreeNodeRoot = XmlMapUtil.getInputTreeNodeRoot((TreeNode) getTargetEditPart().getModel());
                if (inputTreeNodeRoot != null && inputTreeNodeRoot.eContainer() instanceof InputXmlTree) {
                    isLookup = ((InputXmlTree) inputTreeNodeRoot.eContainer()).isLookup();
                }
            }

            if (!(getTargetEditPart() instanceof OutputTreeNodeEditPart) && !(getTargetEditPart() instanceof VarNodeEditPart)
                    && !isLookup) {
                event.detail = DND.DROP_NONE;
            } else if (getTargetEditPart() instanceof OutputTreeNodeEditPart) {
                OutputTreeNodeEditPart nodePart = (OutputTreeNodeEditPart) getTargetEditPart();
                OutputTreeNode model = (OutputTreeNode) nodePart.getModel();
                if (XmlMapUtil.DOCUMENT.equals(model.getType())) {
                    event.detail = DND.DROP_NONE;
                }

                if (NodeType.ATTRIBUT.equals(model.getNodeType()) || NodeType.NAME_SPACE.equals(model.getNodeType())) {
                    event.detail = DND.DROP_NONE;
                }
                if (!model.getChildren().isEmpty() && targetFigure instanceof ExpressionFigure) {
                    event.detail = DND.DROP_NONE;
                }
            } else if (getTargetEditPart() instanceof TreeNodeEditPart) {
                if (!isLookup) {
                    event.detail = DND.DROP_NONE;
                }
                TreeNodeEditPart nodePart = (TreeNodeEditPart) getTargetEditPart();
                TreeNode model = (TreeNode) nodePart.getModel();

                if (XmlMapUtil.DOCUMENT.equals(model.getType())) {
                    event.detail = DND.DROP_NONE;
                }

                if (!model.getChildren().isEmpty() && targetFigure instanceof ExpressionFigure) {
                    event.detail = DND.DROP_NONE;
                }
            }
        }
    }

    @Override
    protected void handleDrop() {
        super.handleDrop();
    }

}
