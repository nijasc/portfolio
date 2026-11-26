import { ApiError, request, type RequestOptions } from './client.js';
import type { BlogView, Credentials, CurrentUser, Greeting, NewBlog, Problem, VisitCount } from './types.js';

/** @see dev/nilsschlegel/Auth.kt */
export const authLogin = (body: Credentials, options?: RequestOptions) => request<CurrentUser>('POST', '/api/auth/login', { ...options, body, operation: 'auth.login' });
/** @see dev/nilsschlegel/Auth.kt */
export const authLogout = (options?: RequestOptions) => request<void>('POST', '/api/auth/logout', { ...options, operation: 'auth.logout' });
/** @see dev/nilsschlegel/Auth.kt */
export const authGetMe = (options?: RequestOptions) => request<CurrentUser>('GET', '/api/auth/me', { ...options, operation: 'auth.getMe' });
/** @see dev/nilsschlegel/Auth.kt */
export const authRegister = (body: Credentials, options?: RequestOptions) => request<CurrentUser>('POST', '/api/auth/register', { ...options, body, operation: 'auth.register' });

export const auth = {
    /** @see dev/nilsschlegel/Auth.kt */
    login: authLogin,
    /** @see dev/nilsschlegel/Auth.kt */
    logout: authLogout,
    /** @see dev/nilsschlegel/Auth.kt */
    getMe: authGetMe,
    /** @see dev/nilsschlegel/Auth.kt */
    register: authRegister,
};

/** @see dev/nilsschlegel/blog/adapters/web/BlogRoutes.kt */
export const blogList = (options?: RequestOptions) => request<BlogView[]>('GET', '/api/blog', { ...options, operation: 'blog.list' });
/** @see dev/nilsschlegel/blog/adapters/web/BlogRoutes.kt */
export const blogCreate = (body: NewBlog, options?: RequestOptions) => request<BlogView>('POST', '/api/blog', { ...options, body, operation: 'blog.create' });

export const blog = {
    /** @see dev/nilsschlegel/blog/adapters/web/BlogRoutes.kt */
    list: blogList,
    /** @see dev/nilsschlegel/blog/adapters/web/BlogRoutes.kt */
    create: blogCreate,
};

/** @see dev/nilsschlegel/Application.kt */
export const helloList = (options?: RequestOptions) => request<Greeting>('GET', '/api/hello', { ...options, operation: 'hello.list' });

export const hello = {
    /** @see dev/nilsschlegel/Application.kt */
    list: helloList,
};

/** @see dev/nilsschlegel/Database.kt */
export const visitsCreate = (options?: RequestOptions) => request<VisitCount>('POST', '/api/visits', { ...options, operation: 'visits.create' });

export const visits = {
    /** @see dev/nilsschlegel/Database.kt */
    create: visitsCreate,
};

export interface ApiErrors {
    'auth.login': Problem;
    'auth.register': Problem;
}

const errorStatuses: Record<string, number[]> = {
    'auth.login': [401],
    'auth.register': [409],
};

export function isApiError<K extends keyof ApiErrors>(error: unknown, operation: K): error is ApiError<ApiErrors[K]> {
    return error instanceof ApiError && error.operation === operation && (errorStatuses[operation] ?? []).includes(error.status);
}

export const api = { auth, blog, hello, visits };

function bindFetch<T extends object>(target: T, fetch: typeof globalThis.fetch): T {
    const bound: Record<string, unknown> = {};
    for (const [key, value] of Object.entries(target)) {
        if (typeof value !== 'function') {
            bound[key] = bindFetch(value as object, fetch);
            continue;
        }
        const arity = value.length;
        bound[key] = (...args: unknown[]) => {
            const padded = args.slice(0, arity);
            padded.length = arity;
            padded[arity - 1] = { fetch, ...(padded[arity - 1] as RequestOptions | undefined) };
            return value(...padded);
        };
    }
    return bound as T;
}

export function apiWith(event: { fetch: typeof globalThis.fetch }): typeof api {
    return bindFetch(api, event.fetch);
}
