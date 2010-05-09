#include <hack.h>
void choose_windows(const char *s)
{
	register int i;
	for(i=0; winchoices[i].procs; i++)
		if (!strcmpi(s, winchoices[i].procs->name)) {
			windowprocs = *winchoices[i].procs;
			if (winchoices[i].ini_routine) (*winchoices[i].ini_routine)();
			return;
		}

	if (!windowprocs.win_raw_print)
		windowprocs.win_raw_print = def_raw_print;

	raw_printf("Window type %s not recognized.  Choices are:", s);
	for(i=0; winchoices[i].procs; i++)
		raw_printf("        %s", winchoices[i].procs->name);

	if (windowprocs.win_raw_print == def_raw_print)
		terminate(EXIT_SUCCESS);
	wait_synch();
}