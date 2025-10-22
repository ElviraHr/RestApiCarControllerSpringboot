package de.ait.training.controller;

import de.ait.training.model.Car;
import de.ait.training.repository.CarRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Cars", description = "Operations on Cars")
@Slf4j
@RequestMapping("/api/cars")
@RestController
public class RestApiCarController {

    private CarRepository carRepository;

    /* Car carOne = new Car(1, "BMW x5", "black", 25000);
     Car carTwo = new Car(2, "Audi A4", "green", 15000);
     Car carThree = new Car(3, "MB A220", "white", 18000);
     Car carFour = new Car(4, "Ferrari", "red", 250000);

      List<Car> cars = new ArrayList<>();

     public RestApiCarController() {
         cars.add(carOne);
         cars.add(carTwo);
         cars.add(carThree);
         cars.add(carFour);
     */
    public RestApiCarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    /**
     * GET --> api/cars
     *
     * @return возвращает список всех автомобилей
     **/
    @GetMapping
    Iterable<Car> getCars() {
        return carRepository.findAll();
    }

    /**
     * Создает новый автомобиль и добавляет его в лист
     *
     * @param car
     * @return созданный автомобиль
     */
    //тег для отображения в документации вместе с
    @Operation(
            summary = "Create car",
            description = "Create a new car",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Created")
            }
    )
    @PostMapping
    Car postCar(@RequestBody Car car) {
        if (car.getId() <= 0) {
            log.error("Car id must be greater than zero");
            Car errorCar = new Car("000", "000", 9999);
            return errorCar;
        }
        carRepository.save(car);
        log.info("Car posted successfully");
        return car;
    }

    /**
     * Замена существующего автомобиля, если id не найден то создаем новый
     *
     * @param id
     * @param car
     * @return созданный или найденный автомобиль
     */
    @PutMapping("/{id}")
    ResponseEntity<Car> putCar(@PathVariable Long id, @RequestBody Car car) {

        Car foundCar = carRepository.findById(id).orElse(null);

        if (foundCar == null) {
            log.error("Car not found");
        } else {
            log.info("Car {} found", foundCar);
            carRepository.save(car);
        }


        return (foundCar == null)
                ? new ResponseEntity<>(postCar(car), HttpStatus.CREATED)
                : new ResponseEntity<>(car, HttpStatus.OK);
    }

    /**
     * удаляем автомобиль по id
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    void deleteCar(@PathVariable long id) {
        log.info("Delete car with id {}", id);
        carRepository.deleteById(id);
    }

    //homework №5

    /**
     * CET api/cars/color/{color}
     * найти все автомобили по цвету
     *
     * @param color
     * @return Возвращает список найденных автомобилей по параметру {color}. Если не найден, возвращается пустой список
     */
    @Operation(summary = "Get cars by color",
            description = "Returns a list of cars filtered by color",
            responses = @ApiResponse(responseCode = "200", description = "Found cars with color ")

    )
    @GetMapping("/color/{color}")
    ResponseEntity<List<Car>> getCarsByColor(@PathVariable String color) {

        List<Car> filteredCars = carRepository.findCarByColorIgnoreCase(color);
        if (filteredCars.isEmpty()) {
            log.warn("Color {} not found", color);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info("Found {} cars with color {}", filteredCars.size(), color);
        }

        return new ResponseEntity<>(filteredCars, HttpStatus.OK);
    }

    //Homework 5_2

    /**
     * CET api/cars/color/{color}
     * найти все автомобили в заданном диапазоне стоимости
     *
     * @param min - минимальная граница цены,
     * @param max - максимальная граница цены(включительно)
     * @return Возвращает список найденных автомобилей по параметру {color}. Если не найден, возвращается пустой список
     */
    @Operation(summary = "Get cars between min-max price",
            description = "Returns a list of cars filtered by price between min and max price",
            responses = @ApiResponse(responseCode = "200", description = "Found cars price between ")
    )
    @GetMapping("/price/between/{min}/{max}")
    ResponseEntity<List<Car>> getCarsByPriceBetween(@PathVariable double min, @PathVariable double max) {

        List<Car> filteredCars = new ArrayList<>();
        if (max <= min) {
            return new ResponseEntity<>(filteredCars, HttpStatus.BAD_REQUEST);
        }
        filteredCars = carRepository.findCarByPriceBetween(min, max);
        if (filteredCars.isEmpty()) {
            log.warn("Price between {} and {} not found", min, max);
            return new ResponseEntity<>(filteredCars, HttpStatus.NOT_FOUND);
        } else {
            log.info("Found {} cars price between {} and {}", filteredCars.size(), min, max);
        }

        return new ResponseEntity<>(filteredCars, HttpStatus.OK);
    }

    /**
     * CET api/cars/price/under/{max}
     * найти все автомобили стоимостью ниже <max>
     *
     * @param max
     * @return Возвращает список найденных автомобилей стоимостью ниже параметра {max}. Если не найден, возвращается пустой список
     */
    @Operation(summary = "Get cars by color",
            description = "Returns a list of cars filtered by color",
            responses = @ApiResponse(responseCode = "200", description = "Found cars with color ")

    )
    @GetMapping("/price/under/{max}")
    ResponseEntity<List<Car>> getCarsByPriceUnder(@PathVariable double max) {

        List<Car> filteredCars = carRepository.findCarByPriceBefore(max);
        if (filteredCars.isEmpty()) {
            log.warn("Price under {} not found", max);
            return new ResponseEntity<>(filteredCars, HttpStatus.NOT_FOUND);
        } else {
            log.info("Found {} cars with under price {}", filteredCars.size(), max);
        }

        return new ResponseEntity<>(filteredCars, HttpStatus.OK);
    }

    /**
     * CET api/cars/price/over/{min}
     * найти все автомобили стоимостью выше <min>
     *
     * @param min
     * @return Возвращает список найденных автомобилей стоимостью выше параметра {min}. Если не найден, возвращается пустой список
     */
    @Operation(summary = "Get cars by color",
            description = "Returns a list of cars filtered by color",
            responses = @ApiResponse(responseCode = "200", description = "Found cars with color ")

    )
    @GetMapping("/price/over/{min}")
    ResponseEntity<List<Car>> getCarsByPriceOver(@PathVariable double min) {

        List<Car> filteredCars = carRepository.findCarByPriceAfter(min);
        if (filteredCars.isEmpty()) {
            log.warn("Price over {} not found", min);
            return new ResponseEntity<>(filteredCars, HttpStatus.NOT_FOUND);
        } else {
            log.info("Found {} cars with price over {}", filteredCars.size(), min);
        }

        return new ResponseEntity<>(filteredCars, HttpStatus.OK);
    }


}
