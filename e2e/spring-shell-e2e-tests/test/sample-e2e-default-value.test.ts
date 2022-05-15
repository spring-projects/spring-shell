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

// e2e default value commands
describe('e2e commands default-value', () => {
  let cli: Cli;
  let command: string;
  let options: string[] = [];

  /**
   * testDefaultValue
   */
  const annoDefaultWithoutArgReturnsHiDesc = 'default without arg returns hi (anno)';
  const annoDefaultWithoutArgCommand = ['e2e anno default-value'];
  const annoDefaultWithoutArgReturnsHi = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello hi')]));
    });
    await expect(cli.exitCode()).resolves.toBe(0);
  };

  /**
   * testDefaultValueRegistration
   */
  const regDefaultWithoutArgReturnsHiDesc = 'default without arg returns hi (reg)';
  const regDefaultWithoutArgCommand = ['e2e reg default-value'];
  const regOptionalWithoutArgReturnsHi = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello hi')]));
    });
    await expect(cli.exitCode()).resolves.toBe(0);
  };

  /**
   * testDefaultValue
   */
  const annoDefaultWithArgReturnsFooDesc = 'default with arg returns foo (anno)';
  const annoDefaultWithArgCommand = ['e2e anno default-value --arg1 foo'];
  const annoDefaultWithArgReturnsFoo = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello foo')]));
    });
    await expect(cli.exitCode()).resolves.toBe(0);
  };

  /**
   * testDefaultValueRegistration
   */
  const regDefaultWithArgReturnsFooDesc = 'default with arg returns foo (reg)';
  const regDefaultWithArgCommand = ['e2e reg optional-value --arg1 foo'];
  const regDefaultWithArgReturnsFoo = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello foo')]));
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
      annoDefaultWithoutArgReturnsHiDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultWithoutArgCommand]
        });
        await annoDefaultWithoutArgReturnsHi(cli);
      },
      testTimeout
    );

    it(
      regDefaultWithoutArgReturnsHiDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultWithoutArgCommand]
        });
        await regOptionalWithoutArgReturnsHi(cli);
      },
      testTimeout
    );

    it(
      annoDefaultWithArgReturnsFooDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultWithArgCommand]
        });
        await annoDefaultWithArgReturnsFoo(cli);
      },
      testTimeout
    );

    it(
      regDefaultWithArgReturnsFooDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultWithArgCommand]
        });
        await regDefaultWithArgReturnsFoo(cli);
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
      annoDefaultWithoutArgReturnsHiDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultWithoutArgCommand]
        });
        await annoDefaultWithoutArgReturnsHi(cli);
      },
      testTimeout
    );

    it(
      regDefaultWithoutArgReturnsHiDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultWithoutArgCommand]
        });
        await regOptionalWithoutArgReturnsHi(cli);
      },
      testTimeout
    );

    it(
      annoDefaultWithArgReturnsFooDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultWithArgCommand]
        });
        await annoDefaultWithArgReturnsFoo(cli);
      },
      testTimeout
    );

    it(
      regDefaultWithArgReturnsFooDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultWithArgCommand]
        });
        await regDefaultWithArgReturnsFoo(cli);
      },
      testTimeout
    );
  });
});
