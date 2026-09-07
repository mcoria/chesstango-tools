package net.chesstango.tools.tuning.factories;

import lombok.Getter;
import net.chesstango.evaluation.Evaluator;
import net.chesstango.evaluation.evaluators.EvaluatorByMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Arrays;

/**
 * @author Mauricio Coria
 */
@Getter
public class EvaluatorByMaterialFactory implements GameEvaluatorFactory {
    private static final Logger logger = LoggerFactory.getLogger(EvaluatorByMaterialFactory.class);

    private final String key;

    private final int[] pieceValues;

    public EvaluatorByMaterialFactory(int[] pieceValues) {

        this.pieceValues = pieceValues;

        int computedKey = Arrays.hashCode(pieceValues);

        this.key = Integer.toHexString(computedKey);
    }

    @Override
    public Evaluator createGameEvaluator() {
        EvaluatorByMaterial.EvaluatorByMaterialTable table = new EvaluatorByMaterial.EvaluatorByMaterialTable(key,
                pieceValues[0], pieceValues[1], pieceValues[2], pieceValues[3], pieceValues[4]);
        return new EvaluatorByMaterial(table);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getRepresentation() {
        EvaluatorByMaterial.EvaluatorByMaterialTable obj = new EvaluatorByMaterial.EvaluatorByMaterialTable(key,
                pieceValues[0], pieceValues[1], pieceValues[2], pieceValues[3], pieceValues[4]);

        ObjectMapper mapper = JsonMapper.builder().build();
        return mapper.writeValueAsString(obj);
    }

}
