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
  waitForExpectDefaultInterval
} from '../src/utils';

// all flow commands
describe('flow commands', () => {
  let cli: Cli;
  let command: string;
  let options: string[] = [];

  /**
   * test for flow conditional field2 skips field1
   */
  const flowConditionalField2SkipsFields1Desc = 'flow conditional field2 skips field1';
  const flowConditionalCommand = ['flow', 'conditional'];
  const flowConditionalField2SkipsFields1 = async (cli: Cli) => {
    cli.run();

    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Single1')]));
    });

    await cli.keyDown();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('> Field2')]));
    });

    await cli.keyEnter();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(
        expect.arrayContaining([expect.stringContaining('? Field2 [Default defaultField2Value]')])
      );
    });

    await cli.keyEnter();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Field2 defaultField2Value')]));
    });

    await expect(cli.exitCode()).resolves.toBe(0);
  };

  beforeEach(async () => {
    waitForExpect.defaults.timeout = waitForExpectDefaultTimeout;
    waitForExpect.defaults.interval = waitForExpectDefaultInterval;
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

    it(flowConditionalField2SkipsFields1Desc, async () => {
      cli = new Cli({
        command: command,
        options: [...options, ...flowConditionalCommand]
      });
      await flowConditionalField2SkipsFields1(cli);
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

    it(flowConditionalField2SkipsFields1Desc, async () => {
      cli = new Cli({
        command: command,
        options: [...options, ...flowConditionalCommand]
      });
      await flowConditionalField2SkipsFields1(cli);
    });
  });
});
