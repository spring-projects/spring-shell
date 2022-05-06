import 'jest-extended';
import waitForExpect from 'wait-for-expect';
import { Cli } from 'spring-shell-e2e';
import { nativeDesc, jarDesc, jarCommand, nativeCommand, jarOptions } from '../src/utils';

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
    waitForExpect.defaults.timeout = 3000;
    waitForExpect.defaults.interval = 100;
  }, 300000);

  afterEach(async () => {
    cli?.dispose();
  }, 100000);

  /**
   * native commands
   */
  describe(nativeDesc, () => {
    beforeAll(() => {
      command = nativeCommand;
    });

    it(versionReturnsInfoDesc, async () => {
      cli = new Cli({
        command: command,
        options: [...options, ...versionCommand]
      });
      await versionReturnsInfo(cli);
    });
  });

  /**
   * fatjar commands
   */
  describe(jarDesc, () => {
    beforeAll(() => {
      command = jarCommand;
      options = jarOptions;
    });

    it(versionReturnsInfoDesc, async () => {
      cli = new Cli({
        command: command,
        options: [...options, ...versionCommand]
      });
      await versionReturnsInfo(cli);
    });
  });
});
