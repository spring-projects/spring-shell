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

describe('e2e commands arity-boolean-default-true', () => {
  let cli: Cli;
  let command: string;
  let options: string[] = [];

  /**
   * testBooleanArity1DefaultTrue
   */
  const annoDefaultWithoutOverwriteReturnsTrueDesc = 'default without overwrite returns true (anno)';
  const annoDefaultWithoutOverwriteCommand = ['e2e', 'anno', 'arity-boolean-default-true'];
  const annoDefaultWithoutOverwriteReturnsTrue = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello true')]));
    });
    await expect(cli.exitCode()).resolves.toBe(0);
  };

  /**
   * testBooleanArity1DefaultTrueRegistration
   */
  const regDefaultWithoutOverwriteReturnsTrueDesc = 'default without overwrite returns true (reg)';
  const regDefaultWithoutOverwriteCommand = ['e2e', 'reg', 'arity-boolean-default-true'];
  const regOptionalWithoutOverwriteReturnsTrue = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello true')]));
    });
    await expect(cli.exitCode()).resolves.toBe(0);
  };

  /**
   * testBooleanArity1DefaultTrue
   */
   const annoDefaultWithOverwriteFalseReturnsFalseDesc = 'default with overwrite false returns false (anno)';
   const annoDefaultWithOverwriteFalseCommand = ['e2e', 'anno', 'arity-boolean-default-true', '--overwrite', 'false'];
   const annoDefaultWithOverwriteFalseReturnsFalse = async (cli: Cli) => {
     cli.run();
     await waitForExpect(async () => {
       const screen = cli.screen();
       expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello false')]));
     });
     await expect(cli.exitCode()).resolves.toBe(0);
   };

   /**
    * testBooleanArity1DefaultTrueRegistration
    */
   const regDefaultWithOverwriteFalseReturnsFalseDesc = 'default with overwrite false returns false (reg)';
   const regDefaultWithOverwriteFalseCommand = ['e2e', 'reg', 'arity-boolean-default-true', '--overwrite', 'false'];
   const regOptionalWithOverwriteFalseReturnsFalse = async (cli: Cli) => {
     cli.run();
     await waitForExpect(async () => {
       const screen = cli.screen();
       expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello false')]));
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
      annoDefaultWithoutOverwriteReturnsTrueDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultWithoutOverwriteCommand]
        });
        await annoDefaultWithoutOverwriteReturnsTrue(cli);
      },
      testTimeout
    );

    it(
      regDefaultWithoutOverwriteReturnsTrueDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultWithoutOverwriteCommand]
        });
        await regOptionalWithoutOverwriteReturnsTrue(cli);
      },
      testTimeout
    );

    it(
        annoDefaultWithOverwriteFalseReturnsFalseDesc,
        async () => {
          cli = new Cli({
            command: command,
            options: [...options, ...annoDefaultWithOverwriteFalseCommand]
          });
          await annoDefaultWithOverwriteFalseReturnsFalse(cli);
        },
        testTimeout
      );

      it(
        regDefaultWithOverwriteFalseReturnsFalseDesc,
        async () => {
          cli = new Cli({
            command: command,
            options: [...options, ...regDefaultWithOverwriteFalseCommand]
          });
          await regOptionalWithOverwriteFalseReturnsFalse(cli);
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
      annoDefaultWithoutOverwriteReturnsTrueDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultWithoutOverwriteCommand]
        });
        await annoDefaultWithoutOverwriteReturnsTrue(cli);
      },
      testTimeout
    );

    it(
      regDefaultWithoutOverwriteReturnsTrueDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultWithoutOverwriteCommand]
        });
        await regOptionalWithoutOverwriteReturnsTrue(cli);
      },
      testTimeout
    );

    it(
        annoDefaultWithOverwriteFalseReturnsFalseDesc,
        async () => {
          cli = new Cli({
            command: command,
            options: [...options, ...annoDefaultWithOverwriteFalseCommand]
          });
          await annoDefaultWithOverwriteFalseReturnsFalse(cli);
        },
        testTimeout
      );

      it(
        regDefaultWithOverwriteFalseReturnsFalseDesc,
        async () => {
          cli = new Cli({
            command: command,
            options: [...options, ...regDefaultWithOverwriteFalseCommand]
          });
          await regOptionalWithOverwriteFalseReturnsFalse(cli);
        },
        testTimeout
      );
  });
});
