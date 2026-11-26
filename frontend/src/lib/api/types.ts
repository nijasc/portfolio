declare const iso: unique symbol;

const pad = (value: number, width = 2) => String(value).padStart(width, '0');

export type IsoDate = string & { readonly [iso]: 'date' };

export const IsoDate = {
    parse: (value: IsoDate): Date => {
        const [year, month, day] = value.split('-').map(Number);
        const date = new Date(year, month - 1, day);
        date.setFullYear(year);
        return date;
    },
    from: (date: Date): IsoDate => `${pad(date.getFullYear(), 4)}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}` as IsoDate,
    today: (): IsoDate => IsoDate.from(new Date()),
};

export type IsoTime = string & { readonly [iso]: 'time' };

export const IsoTime = {
    from: (date: Date): IsoTime => `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}` as IsoTime,
};

export type IsoDateTime = string & { readonly [iso]: 'date-time' };

export const IsoDateTime = {
    parse: (value: IsoDateTime): Date => new Date(value),
    from: (date: Date): IsoDateTime => date.toISOString() as IsoDateTime,
    fromLocal: (date: Date): IsoDateTime => `${IsoDate.from(date)}T${IsoTime.from(date)}` as IsoDateTime,
    now: (): IsoDateTime => IsoDateTime.from(new Date()),
};

export interface BlogView {
    id: string;
    name: string;
}

export const BlogView = {
    empty: (): BlogView => ({
        id: '',
        name: '',
    }),
    bounds: {
        name: { autocomplete: 'name' },
    },
};

export interface Credentials {
    username: string;
    password: string;
}

export const Credentials = {
    empty: (): Credentials => ({
        username: '',
        password: '',
    }),
    bounds: {
        username: { maxlength: 64, autocomplete: 'username' },
        password: { required: true, minlength: 12, maxlength: 200, autocomplete: 'current-password', type: 'password' },
    },
    validate: (value: Credentials): Record<string, string[]> => {
        const errors: Record<string, string[]> = {};
        if (typeof value.username === 'string') {
            const length = [...value.username].length;
            if (length > 64) addError(errors, 'username', 'Must be at most 64 characters.');
        }
        if (typeof value.password === 'string') {
            const length = [...value.password].length;
            if (length < 12) addError(errors, 'password', 'Must be at least 12 characters.');
            if (length > 200) addError(errors, 'password', 'Must be at most 200 characters.');
        }
        return errors;
    },
};

export interface CurrentUser {
    username: string;
}

export const CurrentUser = {
    empty: (): CurrentUser => ({
        username: '',
    }),
    bounds: {
        username: { autocomplete: 'username' },
    },
};

export interface Greeting {
    message: string;
}

export const Greeting = {
    empty: (): Greeting => ({
        message: '',
    }),
};

export interface NewBlog {
    name: string;
}

export const NewBlog = {
    empty: (): NewBlog => ({
        name: '',
    }),
    bounds: {
        name: { maxlength: 200, autocomplete: 'name' },
    },
    validate: (value: NewBlog): Record<string, string[]> => {
        const errors: Record<string, string[]> = {};
        if (typeof value.name === 'string') {
            const length = [...value.name].length;
            if (length > 200) addError(errors, 'name', 'Must be at most 200 characters.');
        }
        return errors;
    },
};

export interface Problem {
    message: string;
}

export const Problem = {
    empty: (): Problem => ({
        message: '',
    }),
};

export interface VisitCount {
    visits: number;
    at: IsoDateTime;
}

export const VisitCount = {
    empty: (): VisitCount => ({
        visits: 0,
        at: '' as IsoDateTime,
    }),
    bounds: {
        visits: { required: true },
    },
};

function addError(errors: Record<string, string[]>, field: string, message: string): void {
    (errors[field] ??= []).push(message);
}
