import * as pty from 'node-pty';
import * as os from 'os';
import { Terminal } from 'xterm-headless';

export interface CliOptions {
  command: string;
  options?: string[];
  keyWait?: number;
  cols?: number;
  rows?: number;
}

export class Cli {
  private isDisposed: boolean = false;
  private pty: pty.IPty | undefined;
  private term: Terminal | undefined;
  private keyWait: number = 500;
  private cols: number = 80;
  private rows: number = 20;
  private exit: Promise<number> | undefined;
  private hasExit: boolean = false;

  constructor(private options: CliOptions) {
    if (options.keyWait) {
      this.keyWait = options.keyWait;
    }
    if (options.cols) {
      this.cols = options.cols;
    }
    if (options.rows) {
      this.rows = options.rows;
    }
  }

  public run(): void {
    const isWindows = (os.platform() === 'win32')
    this.term = new Terminal({
      allowProposedApi: true,
      windowsMode: isWindows,
      cols: this.cols,
      rows: this.rows
    });
    this.pty = pty.spawn(this.options.command, this.options.options || [], {
      name: 'xterm-256color',
      cols: this.cols,
      rows: this.rows,
      useConpty: false
    });
    this.pty.onData(data => {
      this.term?.write(data);
    });
    this.exit = new Promise(resolve => {
      this.pty?.onExit(data => {
        this.hasExit = true;
        resolve(data.exitCode);
      });
    });
  }

  public exitCode(): Promise<number> {
    if (!this.exit) {
      return Promise.reject('cli has not been started');
    }
    return this.exit;
  }

  public screen(): string[] {
    const l = this.term?.buffer.active.length || 0;
    const ret: string[] = [];
    for (let index = 0; index < l; index++) {
      const line = this.term?.buffer.active.getLine(index)?.translateToString();
      if (line) {
        ret.push(line);
      }
    }
    return ret;
  }

  public async keyText(data: string, wait: number): Promise<Cli> {
    if (!this.pty) {
      return Promise.reject('cli has not been started');
    }
    this.pty.write(data);
    await this.doWait(wait);
    return this;
  }

  public async keyUp(wait?: number): Promise<Cli> {
    if (!this.pty) {
      return Promise.reject('cli has not been started');
    }
    this.pty.write('\x1BOA');
    await this.doWait(wait);
    return this;
  }

  public async keyDown(wait?: number): Promise<Cli> {
    if (!this.pty) {
      return Promise.reject('cli has not been started');
    }
    this.pty.write('\x1BOB');
    await this.doWait(wait);
    return this;
  }

  public async keyEnter(wait?: number): Promise<Cli> {
    if (!this.pty) {
      return Promise.reject('cli has not been started');
    }
    this.pty.write('\x0D');
    await this.doWait(wait);
    return this;
  }

  public dispose(): void {
    if (this.isDisposed) {
      return;
    }
    try {
      this.pty?.kill();
    } catch (error) {
      console.log(error);
    }
    this.term?.dispose();
    this.isDisposed = true;
  }

  private async doWait(wait?: number): Promise<void> {
    await this.sleep(wait || this.keyWait);
  }

  private async sleep(ms: number): Promise<void> {
    return new Promise(r => setTimeout(r, ms));
  }
}
