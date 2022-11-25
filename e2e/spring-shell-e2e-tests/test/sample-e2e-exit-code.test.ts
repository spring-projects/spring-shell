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

describe('e2e commands exit-code', () => {
  let cli: Cli;
  let command: string;
  let options: string[] = [];

  const missingArgRet2Desc = 'missing arg returns 2';
  const missingArgRet2Command = ['e2e', 'reg', 'exit-code'];
  const missingArgRet2 = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Missing mandatory option')]));
    });
    await expect(cli.exitCode()).resolves.toBe(2);
  };

  const customArgRet3Desc = 'custom arg returns 3';
  const customArgRet3Command = ['e2e', 'reg', 'exit-code', '--arg1', 'ok'];
  const customArgRet3 = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('ok')]));
    });
    await expect(cli.exitCode()).resolves.toBe(3);
  };

  const customArgRet4Desc = 'custom arg returns 4';
  const customArgRet4Command = ['e2e', 'reg', 'exit-code', '--arg1', 'fun'];
  const customArgRet4 = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('fun')]));
    });
    await expect(cli.exitCode()).resolves.toBe(4);
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
      missingArgRet2Desc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...missingArgRet2Command]
        });
        await missingArgRet2(cli);
      },
      testTimeout
    );

    it(
      customArgRet3Desc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...customArgRet3Command]
        });
        await customArgRet3(cli);
      },
      testTimeout
    );

    it(
      customArgRet4Desc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...customArgRet4Command]
        });
        await customArgRet4(cli);
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
      missingArgRet2Desc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...missingArgRet2Command]
        });
        await missingArgRet2(cli);
      },
      testTimeout
    );

    it(
      customArgRet3Desc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...customArgRet3Command]
        });
        await customArgRet3(cli);
      },
      testTimeout
    );

    it(
      customArgRet4Desc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...customArgRet4Command]
        });
        await customArgRet4(cli);
      },
      testTimeout
    );
  });
});
