package to.wetransform.hale.rlp.cli.match

import java.util.List

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.cli.HaleCLIUtil;
import eu.esdihumboldt.hale.common.core.io.project.model.Project
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;
import eu.esdihumboldt.util.cli.Command
import eu.esdihumboldt.util.cli.CommandContext
import to.wetransform.halecli.project.ProjectHelper;
import to.wetransform.halecli.util.SchemaCLI;

class MatchSchemasCommand implements Command {

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

    // generate mapping between schemas
    SchemaMatcher matcher = new PostNASSchemaMatcher()
    Alignment alignment = matcher.generateSchemaMatching(refSchema, targetSchema)

    // save project
    Project project = new Project()
    project.author = 'Generated'
    project.name = 'Generated schema-to-schema mapping'

    File projectOut = new File('schemas-mapping.halex')
    def output = new FileIOSupplier(projectOut)

    def sourceSS = new DefaultSchemaSpace()
    sourceSS.addSchema(refSchema)

    def targetSS = new DefaultSchemaSpace()
    targetSS.addSchema(targetSchema)

    ReportHandler reports = HaleCLIUtil.createReportHandler()

    ProjectHelper.saveProject(project, alignment, sourceSS, targetSS, output, reports, 'halex')

    return 0
  }

  final String shortDescription = '''Generate a mapping from a reference schema (e.g. AAA XSD) to a
target schema (e.g. PostNAS) based on a fixed set of pre-defined rules'''

}
