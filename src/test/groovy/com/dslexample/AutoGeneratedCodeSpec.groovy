package com.dslexample

import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobParent
import javaposse.jobdsl.dsl.MemoryJobManagement
import javaposse.jobdsl.dsl.helpers.publisher.BuildFlowPublisherContext
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext
import spock.lang.Specification

/**
 * testing the Automatically Generated DSL  without emulate a Jenkins
 * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Testing-DSL-Scripts">Testing DSL Scripts</a>
 */
@Mixin(JobSpecMixin)
class AutoGeneratedCodeSpec extends Specification {

    JobParent jobParent = createJobParentFor(createJobManagementWithStubbedGeneratedDsl())

    def 'call rundeck with default values'() {
        given:
        when:
            Job job = new RundeckJobBuilder(
                    name: 'rundeck-project',
                    rundeckTask: 'sampleRundeckTask'
            ).build(jobParent)
        then:
            with(job.node) {
                name() == 'project'
                def rundeckNotifierNode = publishers.'org.jenkinsci.plugins.rundeck.RundeckNotifier'
                rundeckNotifierNode.jobId[0].value() == 'sampleRundeckTask'
                rundeckNotifierNode.options[0].value().isEmpty()
                rundeckNotifierNode.nodeFilters[0].value().isEmpty()
                rundeckNotifierNode.tags[0].value() == ''
                rundeckNotifierNode.shouldWaitForRundeckJob[0].value() == false
                rundeckNotifierNode.shouldFailTheBuild[0].value() == false
                rundeckNotifierNode.includeRundeckLogs[0].value() == false
            }

    }

    protected JobManagement createJobManagementWithStubbedGeneratedDsl() {
        MemoryJobManagement jm = Spy(MemoryJobManagement)
        stubGeneratedDsl(jm)
        return jm
    }

    private void stubGeneratedDsl(JobManagement jm) {
        jm.callExtension('rundeckNotifier', _, BuildFlowPublisherContext, _) >> { List args ->
            return createRundeckNode(args)
        }
        jm.callExtension('rundeckNotifier', _, PublisherContext, _) >> { List args ->
            return createRundeckNode(args)
        }
    }

    private Node createRundeckNode(List args) {
        Closure rundeckContextClosure = args.last().first() as Closure
        return new NodeBuilder().'org.jenkinsci.plugins.rundeck.RundeckNotifier'(rundeckContextClosure)
    }
}
