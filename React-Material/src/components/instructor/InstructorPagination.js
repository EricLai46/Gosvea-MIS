import React from 'react';
import { Box, Pagination } from '@mui/material';

const InstructorPagination = ({ totalPages, currentPage, handlePageChange }) => {
  return (
    <Box display="flex" justifyContent="center" mt={3}>
      <Pagination
        count={totalPages}
        page={currentPage}
        onChange={handlePageChange}
      />
    </Box>
  );
};

export default InstructorPagination;