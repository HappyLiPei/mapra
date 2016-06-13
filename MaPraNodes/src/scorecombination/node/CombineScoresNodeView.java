package scorecombination.node;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "CombineScores" Node.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class CombineScoresNodeView extends NodeView<CombineScoresNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link CombineScoresNodeModel})
     */
    protected CombineScoresNodeView(final CombineScoresNodeModel nodeModel) {
        super(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        CombineScoresNodeModel nodeModel = (CombineScoresNodeModel)getNodeModel();
        assert nodeModel != null;        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {
    }

}

