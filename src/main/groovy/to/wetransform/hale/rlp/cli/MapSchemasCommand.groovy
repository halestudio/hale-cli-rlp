package to.wetransform.hale.rlp.cli

import java.util.List

import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.util.cli.Command
import eu.esdihumboldt.util.cli.CommandContext
import to.wetransform.halecli.util.SchemaCLI;

class MapSchemasCommand implements Command {

  @Override
  public int run(List<String> args, CommandContext context) {
    CliBuilder cli = new CliBuilder(usage : "${context.baseCommand} [options] [...]")

    cli._(longOpt: 'help', 'Show this help')

    // options for schemas
    SchemaCLI.loadSchemaOptions(cli, 'reference-schema', 'The reference schema')
    SchemaCLI.loadSchemaOptions(cli, 'target-schema', 'The target schema')

    OptionAccessor options = cli.parse(args)

    if (options.help) {
      cli.usage()
      return 0
    }

    // load schemas
    Schema refSchema = SchemaCLI.loadSchema(options, 'reference-schema')
    assert refSchema
    Schema targetSchema = SchemaCLI.loadSchema(options, 'target-schema')
    assert targetSchema

    //TODO

    return 0
  }

  final String shortDescription = '''Generate a mapping from a reference schema (e.g. AAA XSD) to a
target schema (e.g. PostNAS) based on a fixed set of pre-defined rules'''

}
