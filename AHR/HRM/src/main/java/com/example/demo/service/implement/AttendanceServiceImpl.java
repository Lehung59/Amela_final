package com.example.demo.service.implement;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.User;
import com.example.demo.form.AttendanceForm;
import com.example.demo.form.SearchForm;
import com.example.demo.repository.AttendanceRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.AttendanceService;
import com.example.demo.utils.DateUtils;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.demo.constant.Constants.TIME_LUNCH;
import static com.example.demo.constant.Constants.TIME_START_WORK;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepo attendanceRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final DateUtils dateUtils;

    public AttendanceServiceImpl(AttendanceRepo attendanceRepo, UserRepo userRepo, DateUtils dateUtils){
        this.attendanceRepo = attendanceRepo;
        this.userRepo = userRepo;
        this.modelMapper = new ModelMapper();
        this.dateUtils = dateUtils;
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceRepo.findAll();
    }

    @Override
    public Attendance getAttendanceById(int id) throws Exception {
        Optional<Attendance> attendance = attendanceRepo.findById(id);
        if(attendance.isEmpty()) {
            throw new Exception("Attendance not found with ID: " + id);

        }
        return attendance.get();
    }

    @Override
    @Transactional
    public void saveAttendance(AttendanceForm attendanceForm) throws Exception {
        Attendance attendance = mapFormToEntity(attendanceForm);
        if(attendance.getDateCheck()==null) throw new Exception("Thiếu ngày chấm công");
        LocalDate checkDate = attendance.getDateCheck();
        String email = attendance.getUser().getEmail();
        User user = userRepo.findByEmail(email).get();
        Attendance newObj = new Attendance();
        if (attendance.isAllowed()) {
            newObj.setAllowed(true);
            newObj.setNote(attendance.getNote());
        } else {
            newObj.setAllowed(false);
            newObj.setNote(null);
        }
        newObj.setDateCheck(checkDate);
        newObj.setUser(user);
        newObj.setNote(attendance.getNote());
        if(attendance.getStatus().equals(Attendance.AttendanceStatus.MISS) ){
            newObj.setStatus(Attendance.AttendanceStatus.MISS);
        } else if(attendance.getStatus().equals(Attendance.AttendanceStatus.LATE) ){
            newObj.setStatus(Attendance.AttendanceStatus.LATE);
        } else if(attendance.getTimeCheckIn().isAfter(TIME_START_WORK)){
            newObj.setStatus(Attendance.AttendanceStatus.LATE);
        } else {
            newObj.setStatus(Attendance.AttendanceStatus.ONTIME);
        }
        newObj.setWorkTime(countWorkTime(newObj));


        attendanceRepo.save(newObj);
    }

    @Override
    @Transactional
    public void updateAttendance(AttendanceForm attendanceForm) {
        Attendance attendance = mapFormToEntity(attendanceForm);
        Attendance oldObj = attendanceRepo.findById(attendance.getId()).get();
        oldObj.setDateCheck(attendance.getDateCheck());
        if(attendance.getTimeCheckOut() != null) oldObj.setTimeCheckOut(attendance.getTimeCheckOut());
        if(attendance.getTimeCheckIn() != null) oldObj.setTimeCheckIn(attendance.getTimeCheckIn());
        if(!attendance.getNote().isEmpty()) oldObj.setNote(attendance.getNote());
        if(attendance.getStatus() != null) {
            oldObj.setStatus(attendance.getStatus());
        }
        else {
            assert attendance.getTimeCheckIn() != null;
            if (attendance.getTimeCheckIn().isBefore(TIME_START_WORK)) {
                oldObj.setStatus(Attendance.AttendanceStatus.ONTIME);
            } else if(attendance.getTimeCheckIn().isAfter(TIME_START_WORK)) {
                oldObj.setStatus(Attendance.AttendanceStatus.LATE);
            }
        }
//        if(attendance.getStatus() != null) oldObj.setStatus(attendance.getStatus());
//        oldObj.setAllowed(attendance.isAllowed());

        if (attendance.isAllowed()) {
            oldObj.setAllowed(true);
            oldObj.setNote(attendance.getNote());
        } else {
            oldObj.setAllowed(false);
            oldObj.setNote(null);
        }
            oldObj.setWorkTime(countWorkTime(oldObj));

        attendanceRepo.save(oldObj);
    }

    @Override
    @Transactional
    public void deleteAttendance(int id) throws Exception {
        Optional<Attendance> attendance = attendanceRepo.findById(id);
        if(attendance.isEmpty()) {
            throw new Exception("Attendance not found with ID: " + id);

        }
        attendanceRepo.deleteById(id);
    }

    @Override
    @Transactional
    public AttendanceForm setCheckIn(int id) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now().withSecond(0).withNano(0); ;
        User user = userRepo.findById(id).get();

        Optional<Attendance> attendance = attendanceRepo.findByDateAndUserId(date,id);
        if(attendance.isEmpty()) {
            Attendance newObj = Attendance.builder()
                    .dateCheck(date)
                    .timeCheckIn(time)
                    .timeCheckOut(time)
                    .allowed(false)
                    .status(time.isAfter(TIME_START_WORK) ? Attendance.AttendanceStatus.LATE : Attendance.AttendanceStatus.ONTIME)
                    .user(user)
                    .note(null)
                    .workTime(0)
                    .build();
            return mapEntityToForm(attendanceRepo.save(newObj));
        }
        if (attendance.get().getTimeCheckIn() == null){
            attendance.get().setTimeCheckIn(time);
        }
        attendance.get().setTimeCheckOut(time);
        attendance.get().setWorkTime(countWorkTime(attendance.get()));
        return mapEntityToForm(attendanceRepo.save(attendance.get()));

    }

    @Override
    public Page<Attendance> getAllAttendancePaginable(SearchForm searchForm) {
        int page = searchForm.getPage();
        int size = searchForm.getSize();
        int year = searchForm.getYear();
        if(searchForm.getKeyword() == "") searchForm.setKeyword(null);
        if(searchForm.getToDate()!=null && searchForm.getFromDate()!=null){
            searchForm.setMonth(null);
            searchForm.setYear(null);
        } else if(searchForm.getMonth()!=null){
            searchForm.setFromDate(null);
            searchForm.setToDate(null);
        }

        Pageable paging = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<Attendance> pageTuts;
        LocalDate toDate;
        LocalDate fromDate;

        if(searchForm.getKeyword()!=null){
            String keyword = searchForm.getKeyword();
            if(searchForm.getMonth()==null && searchForm.getToDate()==null && searchForm.getFromDate()==null){
                pageTuts = attendanceRepo.findAllAttendance(paging,keyword) ;
            } else {
                if(searchForm.getMonth()!=null) {
                    int month = searchForm.getMonth();
                    fromDate = DateUtils.getFirstDayOfMonth(year, month);
                    toDate = DateUtils.getLastDayOfMonth(year, month);
                } else {
                    toDate = searchForm.getToDate();
                    System.out.println(toDate.toString());
                    fromDate = searchForm.getFromDate();
                }

                pageTuts = attendanceRepo.findAllAttendance(paging,keyword,fromDate,toDate) ;
            }
        } else {
            if(searchForm.getMonth()==null && searchForm.getToDate()==null && searchForm.getFromDate()==null){
                pageTuts = attendanceRepo.findAllAttendanceNoKeyword(paging) ;
            } else{
                if(searchForm.getMonth()!=null){
                    int month = searchForm.getMonth();
                    fromDate = DateUtils.getFirstDayOfMonth(year, month);
                    toDate = DateUtils.getLastDayOfMonth(year, month);
                } else{
                    toDate = searchForm.getToDate();
                    fromDate = searchForm.getFromDate();
                }
                pageTuts = attendanceRepo.findAllAttendanceNoKeyword(paging,fromDate,toDate) ;

            }
        }
        return pageTuts;
    }





    @Override
    public List<Attendance> findByEmailAndDate(String email, LocalDate dateCheck) {
        return attendanceRepo.findByEmailAndDate(email,dateCheck);
    }

    @Override
    public Page<AttendanceForm> getAllAttendanceByIdPaginable(int page, int size,  int id, LocalDate startDate, LocalDate endDate) {
        Pageable paging = PageRequest.of(page - 1, size, Sort.by("dateCheck").descending());
        Page<Attendance> pageTuts = attendanceRepo.findAllByUserId(paging,id,startDate,endDate);
        return pageTuts.map(this::mapEntityToForm);
    }


    public double countWorkTime(Attendance attendance){
        if(attendance.getStatus().equals(Attendance.AttendanceStatus.LATE) && attendance.isAllowed()){
            attendance.setWorkTime(roundTime(TIME_START_WORK,attendance.getTimeCheckOut()));
        } else if (attendance.getStatus().equals(Attendance.AttendanceStatus.MISS) && attendance.isAllowed()) {
            attendance.setWorkTime(8);
        } else if (attendance.getStatus().equals(Attendance.AttendanceStatus.MISS)) {
            attendance.setWorkTime(0);
        } else {
            attendance.setWorkTime(roundTime(attendance.getTimeCheckIn(),attendance.getTimeCheckOut()));
        }

        return attendance.getWorkTime();
    }

    public double roundTime(LocalTime checkin, LocalTime checkout) {
        System.out.println(checkin);
        System.out.println(checkout);
        Duration duration = Duration.between(checkin, checkout);
        long minutes = duration.toMinutes();

        double roundedHours = Math.ceil(minutes / 15.0) * 0.25;
        if(checkin.isBefore(TIME_LUNCH) && checkout.isAfter(TIME_LUNCH))
            roundedHours-=1.5;
        if(roundedHours > 8){
            System.out.println(roundedHours);
            return 8;
        }
        return roundedHours;
    }

    public Attendance mapFormToEntity(AttendanceForm form) {
        Attendance entity = modelMapper.map(form, Attendance.class);
        return entity;
    }

    public AttendanceForm mapEntityToForm(Attendance entity) {
        AttendanceForm form = modelMapper.map(entity, AttendanceForm.class);
        return form;
    }
}
