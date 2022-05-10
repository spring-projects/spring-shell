import 'jest-extended';
import waitForExpect from 'wait-for-expect';
import { Cli } from 'spring-shell-e2e';
import {
  nativeDesc,
  jarDesc,
  jarCommand,
  nativeCommand,
  jarOptions,
  waitForExpectDefaultTimeout,
  waitForExpectDefaultInterval,
  testTimeout
} from '../src/utils';

// all buildin commands
describe('builtin commands', () => {
  let cli: Cli;
  let command: string;
  let options: string[] = [];

  /**
   * test for version command returns expected info
   */
  const versionReturnsInfoDesc = 'version returns info';
  const versionCommand = ['version'];
  const versionReturnsInfo = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Build Version')]));
    });
    await expect(cli.exitCode()).resolves.toBe(0);
  };

  beforeEach(async () => {
    waitForExpect.defaults.timeout = waitForExpectDefaultTimeout;
    waitForExpect.defaults.interval = waitForExpectDefaultInterval;
  }, testTimeout);

  afterEach(async () => {
    cli?.dispose();
  }, testTimeout);

  /**
   * fatjar commands
   */
   describe(jarDesc, () => {
    beforeAll(() => {
      command = jarCommand;
      options = jarOptions;
    });

    it(
      versionReturnsInfoDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...versionCommand]
        });
        await versionReturnsInfo(cli);
      },
      testTimeout
    );
  });

  /**
   * native commands
   */
  describe(nativeDesc, () => {
    beforeAll(() => {
      command = nativeCommand;
      options = [];
    });

    it(
      versionReturnsInfoDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...versionCommand]
        });
        await versionReturnsInfo(cli);
      },
      testTimeout
    );
  });
});
