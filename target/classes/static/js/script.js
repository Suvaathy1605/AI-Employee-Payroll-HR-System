document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('form[data-confirm]').forEach((form) => {
    form.addEventListener('submit', (event) => {
      if (!window.confirm(form.dataset.confirm)) event.preventDefault();
    });
  });

  document.querySelectorAll('form[action$="/attendance/save"]').forEach((form) => {
    form.addEventListener('submit', (event) => {
      const working = Number(form.elements.workingDays.value);
      const present = Number(form.elements.presentDays.value);
      const leave = Number(form.elements.leaveDays.value);
      if (present + leave > working) {
        event.preventDefault();
        window.alert('Present days plus leave days cannot exceed working days.');
      }
    });
  });
});