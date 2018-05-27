
#ifndef TIMELIB_H
#define TIMELIB_H

inline
void timespec_diff(const struct timespec *start,
                    const struct timespec *stop,
                    struct timespec *result)
{
    if ((stop->tv_nsec - start->tv_nsec) < 0) {
        result->tv_sec = stop->tv_sec - start->tv_sec - 1;
        result->tv_nsec = stop->tv_nsec - start->tv_nsec + 1000000000;
    } else {
        result->tv_sec = stop->tv_sec - start->tv_sec;
        result->tv_nsec = stop->tv_nsec - start->tv_nsec;
    }

    return;
}

inline
unsigned long timespec_diff_ns(const struct timespec *start,
                    const struct timespec *stop)
{
    struct timespec result;
    timespec_diff(start, stop, &result);
    return result.tv_sec * 1000000000 + result.tv_nsec;
}


#endif /* TIMELIB_H */

