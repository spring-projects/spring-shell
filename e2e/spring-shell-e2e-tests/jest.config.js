module.exports = {
  clearMocks: true,
  moduleFileExtensions: ['js', 'ts'],
  testEnvironment: 'node',
  testMatch: ['**/*.test.ts'],
  testRunner: 'jest-circus/runner',
  transform: {
    '^.+\\.ts$': 'ts-jest'
  },
  reporters: ['default', 'jest-junit'],
  verbose: true,
  setupFilesAfterEnv: ['jest-extended', '@alex_neo/jest-expect-message']
}