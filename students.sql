-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 15, 2024 at 08:43 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `diving_simulator`
--

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `No` int(11) NOT NULL,
  `Name` varchar(20) NOT NULL,
  `Experience` varchar(20) NOT NULL,
  `Tank_size` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`No`, `Name`, `Experience`, `Tank_size`) VALUES
(1, 'Joe', 'low', 12),
(2, 'Anna', 'normal', 10),
(3, 'Mike', 'high', 11),
(4, 'Sara', 'very high', 15),
(5, 'Tom', 'low', 8),
(6, 'Lisa', 'normal', 12),
(7, 'John', 'high', 10),
(8, 'Eva', 'very high', 11),
(9, 'Paul', 'low', 15),
(10, 'Nina', 'normal', 8),
(11, 'Sophie', 'low', 10),
(12, 'Max', 'normal', 12),
(13, 'Emily', 'high', 11),
(14, 'Alex', 'very high', 15),
(15, 'Olivia', 'low', 8),
(16, 'Daniel', 'normal', 10),
(17, 'Emma', 'high', 12),
(18, 'William', 'very high', 11),
(19, 'Ava', 'low', 15),
(20, 'Liam', 'normal', 8),
(21, 'Mia', 'high', 10),
(22, 'Noah', 'very high', 12),
(23, 'Charlotte', 'low', 11),
(24, 'James', 'normal', 15),
(25, 'Isabella', 'high', 8);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`No`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `No` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
