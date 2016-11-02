package to.wetransform.hale.rlp.cli.match;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

public interface SchemaMatcher {

  /**
   * Generate a matching between two schemas represented by an alignment.
   *
   * @param refSchema the reference schema
   * @param targetSchema the target schema to map to
   * @return the generated alignment
   */
  Alignment generateSchemaMatching(TypeIndex refSchema, TypeIndex targetSchema);

}
