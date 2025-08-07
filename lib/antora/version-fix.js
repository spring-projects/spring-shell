'use strict'

module.exports.register = function () {
    const logger = this.getLogger('version-fix')
    this.once('contentAggregated', ({ contentAggregate }) => {
        contentAggregate.forEach((componentVersionBucket) => {
            logger.info(`tag=${componentVersionBucket.origins[0].tag} version=${componentVersionBucket.version}`)
            // if it is a tag and a -SNAPSHOT release
            if (componentVersionBucket.origins[0].tag && componentVersionBucket.prerelease === '-SNAPSHOT') {
                // remove prerelease: -SNAPSHOT so it appears as a release
                delete componentVersionBucket.prerelease
                // If asciidoctor attribute name ends with -version, then remove -SNAPSHOT suffix (if present)
                const attrs = componentVersionBucket.asciidoc.attributes
                for (const [name, value] of Object.entries(attrs)) {
                    if (name.endsWith('-version') && value.endsWith('-SNAPSHOT')) {
                        attrs[name] = value.split('-SNAPSHOT').shift()
                        logger.info(`Changing asciidoctor attr ${name} from ${value} to ${attrs[name]}`)
                    }
                }
            }
        })
    })
}
